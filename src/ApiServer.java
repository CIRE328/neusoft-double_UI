/**
 * REST API 服务端
 * 基于 JDK 内置 HttpServer 提供 HTTP 接口，供 Vue Web 前端调用；
 * 封装认证、客户、床位、护理、健康管家及用户管理等业务端点
 */

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import service.*;
import pojo.*;
import dao.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ApiServer {

    private static final Gson gson = new Gson();
    private static final AuthService authService = new AuthService();
    private static final CustomerService customerService = new CustomerService();
    private static final BedService bedService = new BedService();
    private static final NurseService nurseService = new NurseService();
    private static final HousekeeperService housekeeperService = new HousekeeperService();
    private static final UserService userService = new UserService();

    /**
     * 启动 HTTP 服务，监听 8080 端口并注册全部 API 路由
     *
     * @param args 命令行参数（未使用）
     * @throws IOException 创建或启动服务器失败时抛出
     */

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // 认证
        server.createContext("/api/auth/login", new LoginHandler());

        // 客户管理
        server.createContext("/api/customer/list", new CustomerListHandler());
        server.createContext("/api/customer/checkin", new CheckinHandler());
        server.createContext("/api/customer/update", new CustomerUpdateHandler());
        server.createContext("/api/customer/delete", new CustomerDeleteHandler());
        server.createContext("/api/customer/without-housekeeper", new WithoutHousekeeperHandler());
        server.createContext("/api/customer/set-level", new SetCustomerLevelHandler());
        server.createContext("/api/customer/remove-level", new RemoveCustomerLevelHandler());
        server.createContext("/api/customer/nurse-items", new CustomerNurseItemsHandler());
        server.createContext("/api/customer/purchase-item", new PurchaseItemHandler());
        server.createContext("/api/customer/renew-item", new RenewItemHandler());
        server.createContext("/api/customer/item", new DeleteCustomerItemHandler());
        server.createContext("/api/customer", new AddCustomerHandler());

        // 退住申请
        server.createContext("/api/backdown/list", new BackdownListHandler());
        server.createContext("/api/backdown/audit", new BackdownAuditHandler());
        server.createContext("/api/backdown/apply", new BackdownApplyHandler());

        // 外出申请
        server.createContext("/api/outward/list", new OutwardListHandler());
        server.createContext("/api/outward/audit", new OutwardAuditHandler());
        server.createContext("/api/outward/return", new OutwardReturnHandler());
        server.createContext("/api/outward/apply", new OutwardApplyHandler());

        // 床位管理
        server.createContext("/api/bed/statistics", new BedStatisticsHandler());
        server.createContext("/api/bed/rooms", new BedRoomsHandler());
        server.createContext("/api/bed/details", new BedDetailsHandler());
        server.createContext("/api/bed/change", new ChangeBedHandler());
        server.createContext("/api/room/list", new RoomListHandler());
        server.createContext("/api/bed/free", new FreeBedsHandler());

        // 护理管理
        server.createContext("/api/nurse/level/list", new NurseLevelListHandler());
        server.createContext("/api/nurse/level", new NurseLevelSaveHandler());
        server.createContext("/api/nurse/level/items", new LevelItemsHandler());
        server.createContext("/api/nurse/level/item", new LevelItemHandler());
        server.createContext("/api/nurse/item/list", new NurseItemListHandler());
        server.createContext("/api/nurse/item", new NurseItemHandler());
        server.createContext("/api/nurse/record", new NurseRecordHandler());
        server.createContext("/api/nurse/record/list", new NurseRecordListHandler());
        server.createContext("/api/nurse/record/customer", new NurseRecordCustomerHandler());

        // 健康管家
        server.createContext("/api/housekeeper/list", new HousekeeperListHandler());
        server.createContext("/api/housekeeper/assign", new AssignHousekeeperHandler());
        server.createContext("/api/housekeeper/customers", new HousekeeperCustomersHandler());
        server.createContext("/api/housekeeper/my-customers", new MyCustomersHandler());

        // 用户管理
        server.createContext("/api/user/list", new UserListHandler());
        server.createContext("/api/user", new UserHandler());
        server.createContext("/api/user/reset-password", new ResetPasswordHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 8080");
    }

    // ---------- 认证 ----------
    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            Map<String, String> req = gson.fromJson(readBody(exchange), new TypeToken<Map<String, String>>() {}.getType());
            User user = authService.login(req.get("username"), req.get("password"));
            if (user != null) {
                Map<String, Object> data = new HashMap<>();
                data.put("token", "dummy-token");
                data.put("user", user);
                sendSuccess(exchange, data);
            } else {
                sendResponse(exchange, 401, error(401, "用户名或密码错误"));
            }
        }
    }

    // ---------- 客户管理 ----------
    static class CustomerListHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            Map<String, String> params = getQueryParams(exchange.getRequestURI().getQuery());
            String name = params.get("name");
            String type = params.get("type");
            List<Customer> list;
            if (type != null && !type.isEmpty()) {
                list = customerService.findCustomersByType(type);
            } else {
                list = customerService.findCustomersByName(name);
            }
            sendSuccess(exchange, list);
        }
    }

    static class CheckinHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            Customer customer = gson.fromJson(readBody(exchange), Customer.class);
            boolean success = customerService.checkin(customer, customer.getBedId());
            sendSuccess(exchange, Collections.singletonMap("success", success));
        }
    }

    static class CustomerUpdateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"PUT".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            Customer customer = gson.fromJson(readBody(exchange), Customer.class);
            boolean success = customerService.updateCustomer(customer);
            sendSuccess(exchange, Collections.singletonMap("success", success));
        }
    }
    // 新增客户（仅保存基本信息，不涉及入住）
    static class AddCustomerHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            Customer customer = gson.fromJson(readBody(exchange), Customer.class);
            // 设置默认值
            customer.setIsDeleted(0);

            Customer saved = new CustomerDao().insert(customer);
            sendSuccess(exchange, saved);
        }
    }

    static class CustomerDeleteHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"DELETE".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            Map<String, String> params = getQueryParams(exchange.getRequestURI().getQuery());
            Integer id = Integer.parseInt(params.get("id"));
            boolean success = customerService.deleteCustomer(id);
            sendSuccess(exchange, Collections.singletonMap("success", success));
        }
    }

    static class WithoutHousekeeperHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            List<Customer> list = housekeeperService.findCustomersWithoutHousekeeper();
            sendSuccess(exchange, list);
        }
    }

    static class SetCustomerLevelHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            Map<String, Object> req = gson.fromJson(readBody(exchange), new TypeToken<Map<String, Object>>() {}.getType());
            Integer customerId = ((Number) req.get("customerId")).intValue();
            Integer levelId = ((Number) req.get("levelId")).intValue();
            boolean success = nurseService.setCustomerLevel(customerId, levelId);
            sendSuccess(exchange, Collections.singletonMap("success", success));
        }
    }

    static class RemoveCustomerLevelHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            Map<String, Object> req = gson.fromJson(readBody(exchange), new TypeToken<Map<String, Object>>() {}.getType());
            Integer customerId = ((Number) req.get("customerId")).intValue();
            boolean success = nurseService.removeCustomerLevel(customerId);
            sendSuccess(exchange, Collections.singletonMap("success", success));
        }
    }

    static class CustomerNurseItemsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            Map<String, String> params = getQueryParams(exchange.getRequestURI().getQuery());
            Integer customerId = Integer.parseInt(params.get("customerId"));
            List<CustomerNurseItem> items = nurseService.getCustomerNurseItems(customerId);
            NurseContentDao contentDao = new NurseContentDao();
            List<Map<String, Object>> result = new ArrayList<>();
            for (CustomerNurseItem item : items) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", item.getId());
                map.put("itemId", item.getItemId());
                map.put("customerId", item.getCustomerId());
                map.put("nurseNumber", item.getNurseNumber());
                map.put("buyTime", item.getBuyTime());
                map.put("maturityTime", item.getMaturityTime());
                contentDao.findById(item.getItemId()).ifPresent(c -> map.put("nursingName", c.getNursingName()));
                result.add(map);
            }
            sendSuccess(exchange, result);
        }
    }

    static class PurchaseItemHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            try {
                Map<String, Object> req = gson.fromJson(readBody(exchange), new TypeToken<Map<String, Object>>() {}.getType());
                Integer customerId = ((Number) req.get("customerId")).intValue();
                Integer itemId = ((Number) req.get("itemId")).intValue();
                Integer quantity = ((Number) req.get("quantity")).intValue();
                Date maturityTime = new SimpleDateFormat("yyyy-MM-dd").parse((String) req.get("maturityTime"));
                boolean success = nurseService.purchaseNurseItem(customerId, itemId, quantity, maturityTime);
                sendSuccess(exchange, Collections.singletonMap("success", success));
            } catch (Exception e) {
                sendError(exchange, 500, e.getMessage());
            }
        }
    }

    static class RenewItemHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            try {
                Map<String, Object> req = gson.fromJson(readBody(exchange), new TypeToken<Map<String, Object>>() {}.getType());
                Integer customerNurseItemId = ((Number) req.get("customerNurseItemId")).intValue();
                Integer additionalQuantity = ((Number) req.get("additionalQuantity")).intValue();
                Date newMaturity = req.get("newMaturityTime") == null ? null : new SimpleDateFormat("yyyy-MM-dd").parse((String) req.get("newMaturityTime"));
                boolean success = nurseService.renewNurseItem(customerNurseItemId, additionalQuantity, newMaturity);
                sendSuccess(exchange, Collections.singletonMap("success", success));
            } catch (Exception e) {
                sendError(exchange, 500, e.getMessage());
            }
        }
    }

    static class DeleteCustomerItemHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"DELETE".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            Map<String, String> params = getQueryParams(exchange.getRequestURI().getQuery());
            Integer id = Integer.parseInt(params.get("id"));
            boolean success = nurseService.removeCustomerNurseItem(id);
            sendSuccess(exchange, Collections.singletonMap("success", success));
        }
    }

    // ---------- 退住申请 ----------
    static class BackdownListHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            Map<String, String> params = getQueryParams(exchange.getRequestURI().getQuery());
            String name = params.get("name");
            List<BackDown> list = customerService.findBackdownsByCustomerName(name);
            CustomerDao dao = new CustomerDao();
            List<Map<String, Object>> result = new ArrayList<>();
            for (BackDown b : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", b.getId());
                map.put("customerId", b.getCustomerId());
                dao.findById(b.getCustomerId()).ifPresent(c -> map.put("customerName", c.getCustomerName()));
                map.put("retreattype", b.getRetreattype());
                map.put("retreatmentreason", b.getRetreatmentreason());
                map.put("auditstatus", b.getAuditstatus());
                result.add(map);
            }
            sendSuccess(exchange, result);
        }
    }

    static class BackdownAuditHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            Map<String, Object> req = gson.fromJson(readBody(exchange), new TypeToken<Map<String, Object>>() {}.getType());
            Integer id = ((Number) req.get("id")).intValue();
            Boolean approved = (Boolean) req.get("approved");
            boolean success = customerService.auditBackdown(id, approved, "admin");
            sendSuccess(exchange, Collections.singletonMap("success", success));
        }
    }

    static class BackdownApplyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            BackDown backdown = gson.fromJson(readBody(exchange), BackDown.class);
            boolean success = customerService.submitBackdown(backdown);
            sendSuccess(exchange, Collections.singletonMap("success", success));
        }
    }

    // ---------- 外出申请 ----------
    static class OutwardListHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            Map<String, String> params = getQueryParams(exchange.getRequestURI().getQuery());
            String name = params.get("name");
            List<Outward> list = customerService.findOutwardsByCustomerName(name);
            CustomerDao dao = new CustomerDao();
            List<Map<String, Object>> result = new ArrayList<>();
            for (Outward o : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", o.getId());
                map.put("customerId", o.getCustomerId());
                dao.findById(o.getCustomerId()).ifPresent(c -> map.put("customerName", c.getCustomerName()));
                map.put("outgoingreasons", o.getOutgoingreasons());
                map.put("outgoingtime", o.getOutgoingtime());
                map.put("expectedreturntime", o.getExpectedreturntime());
                map.put("actualreturntime", o.getActualreturntime());
                map.put("auditstatus", o.getAuditstatus());
                result.add(map);
            }
            sendSuccess(exchange, result);
        }
    }

    static class OutwardAuditHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            Map<String, Object> req = gson.fromJson(readBody(exchange), new TypeToken<Map<String, Object>>() {}.getType());
            Integer id = ((Number) req.get("id")).intValue();
            Boolean approved = (Boolean) req.get("approved");
            boolean success = customerService.auditOutward(id, approved, "admin");
            sendSuccess(exchange, Collections.singletonMap("success", success));
        }
    }

    static class OutwardReturnHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            try {
                Map<String, Object> req = gson.fromJson(readBody(exchange), new TypeToken<Map<String, Object>>() {}.getType());
                Integer id = ((Number) req.get("id")).intValue();
                Date actualReturn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse((String) req.get("actualReturnTime"));
                boolean success = customerService.returnFromOutward(id, actualReturn);
                sendSuccess(exchange, Collections.singletonMap("success", success));
            } catch (Exception e) {
                sendError(exchange, 500, e.getMessage());
            }
        }
    }

    static class OutwardApplyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            Outward outward = gson.fromJson(readBody(exchange), Outward.class);
            boolean success = customerService.submitOutward(outward);
            sendSuccess(exchange, Collections.singletonMap("success", success));
        }
    }

    // ---------- 床位管理 ----------
    static class BedStatisticsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            Map<String, Integer> stats = bedService.getBedStatistics();
            sendSuccess(exchange, stats);
        }
    }

    static class BedRoomsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            Map<String, String> params = getQueryParams(exchange.getRequestURI().getQuery());
            String floor = params.get("floor");
            List<Map<String, Object>> roomsWithBeds = bedService.getRoomsWithBedsByFloor(floor);
            sendSuccess(exchange, roomsWithBeds);
        }
    }

    static class BedDetailsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            Map<String, String> params = getQueryParams(exchange.getRequestURI().getQuery());
            String name = params.get("name");
            String checkinDateStr = params.get("checkinDate");
            Date checkinDate = null;
            if (checkinDateStr != null && !checkinDateStr.isEmpty()) {
                try {
                    checkinDate = new SimpleDateFormat("yyyy-MM-dd").parse(checkinDateStr);
                } catch (Exception ignored) {}
            }
            String usageStatus = params.get("status");
            List<BedDetails> details = bedService.queryBedDetails(name, checkinDate, usageStatus);
            CustomerDao customerDao = new CustomerDao();
            BedDao bedDao = new BedDao();
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (BedDetails d : details) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", d.getId());
                map.put("customerId", d.getCustomerId());
                customerDao.findById(d.getCustomerId()).ifPresent(c -> map.put("customerName", c.getCustomerName()));
                bedDao.findById(d.getBedId()).ifPresent(b -> map.put("bedNo", b.getBedNo()));
                map.put("startDate", d.getStartDate());
                map.put("endDate", d.getEndDate());
                resultList.add(map);
            }
            sendSuccess(exchange, resultList);
        }
    }

    static class ChangeBedHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            Map<String, Object> req = gson.fromJson(readBody(exchange), new TypeToken<Map<String, Object>>() {}.getType());
            Integer customerId = ((Number) req.get("customerId")).intValue();
            Integer newBedId = ((Number) req.get("newBedId")).intValue();
            boolean success = bedService.changeBed(customerId, newBedId);
            sendSuccess(exchange, Collections.singletonMap("success", success));
        }
    }

    static class RoomListHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            RoomDao roomDao = new RoomDao();
            List<Room> rooms = roomDao.findAll();
            sendSuccess(exchange, rooms);
        }
    }

    static class FreeBedsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            Map<String, String> params = getQueryParams(exchange.getRequestURI().getQuery());
            String roomNoStr = params.get("roomNo");
            if (roomNoStr == null) {
                sendResponse(exchange, 400, error(400, "缺少房间号"));
                return;
            }
            Integer roomNo = Integer.parseInt(roomNoStr);
            BedDao bedDao = new BedDao();
            List<Bed> freeBeds = bedDao.findByRoomNo(roomNo).stream()
                    .filter(b -> b.getBedStatus() == 1)
                    .collect(Collectors.toList());
            sendSuccess(exchange, freeBeds);
        }
    }

    // ---------- 护理管理 ----------
    static class NurseLevelListHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            List<NurseLevel> list = nurseService.findAllNurseLevels();
            sendSuccess(exchange, list);
        }
    }

    static class NurseLevelSaveHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            if (!"POST".equals(method) && !"PUT".equals(method)) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            NurseLevel level = gson.fromJson(readBody(exchange), NurseLevel.class);
            if ("POST".equals(method)) {
                NurseLevel newLevel = nurseService.addNurseLevel(level);
                sendSuccess(exchange, newLevel);
            } else {
                boolean ok = nurseService.updateNurseLevel(level);
                sendSuccess(exchange, Collections.singletonMap("success", ok));
            }
        }
    }

    static class LevelItemsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            Map<String, String> params = getQueryParams(exchange.getRequestURI().getQuery());
            Integer levelId = Integer.parseInt(params.get("levelId"));
            List<NurseContent> items = nurseService.getItemsByLevelId(levelId);
            sendSuccess(exchange, items);
        }
    }

    static class LevelItemHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            if ("POST".equals(method)) {
                Map<String, Object> req = gson.fromJson(readBody(exchange), new TypeToken<Map<String, Object>>() {}.getType());
                Integer levelId = ((Number) req.get("levelId")).intValue();
                Integer itemId = ((Number) req.get("itemId")).intValue();
                boolean ok = nurseService.addItemToLevel(levelId, itemId);
                sendSuccess(exchange, Collections.singletonMap("success", ok));
            } else if ("DELETE".equals(method)) {
                Map<String, String> params = getQueryParams(exchange.getRequestURI().getQuery());
                Integer levelId = Integer.parseInt(params.get("levelId"));
                Integer itemId = Integer.parseInt(params.get("itemId"));
                boolean ok = nurseService.removeItemFromLevel(levelId, itemId);
                sendSuccess(exchange, Collections.singletonMap("success", ok));
            } else {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
            }
        }
    }

    static class NurseItemListHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            Map<String, String> params = getQueryParams(exchange.getRequestURI().getQuery());
            String name = params.get("name");
            String statusStr = params.get("status");
            List<NurseContent> list;
            if (statusStr != null && !statusStr.isEmpty()) {
                list = nurseService.findNurseContentsByStatus(Integer.parseInt(statusStr));
            } else {
                list = nurseService.findNurseContentsByName(name);
            }
            sendSuccess(exchange, list);
        }
    }

    static class NurseItemHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            if ("POST".equals(method)) {
                NurseContent item = gson.fromJson(readBody(exchange), NurseContent.class);
                NurseContent saved = nurseService.addNurseContent(item);
                sendSuccess(exchange, saved);
            } else if ("PUT".equals(method)) {
                NurseContent item = gson.fromJson(readBody(exchange), NurseContent.class);
                boolean ok = nurseService.updateNurseContent(item);
                sendSuccess(exchange, Collections.singletonMap("success", ok));
            } else if ("DELETE".equals(method)) {
                Map<String, String> params = getQueryParams(exchange.getRequestURI().getQuery());
                Integer id = Integer.parseInt(params.get("id"));
                boolean ok = nurseService.deleteNurseContent(id);
                sendSuccess(exchange, Collections.singletonMap("success", ok));
            } else {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
            }
        }
    }

    static class NurseRecordHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            if ("POST".equals(method)) {
                Map<String, Object> req = gson.fromJson(readBody(exchange), new TypeToken<Map<String, Object>>() {}.getType());
                Integer customerId = ((Number) req.get("customerId")).intValue();
                Integer itemId = ((Number) req.get("itemId")).intValue();
                Integer nursingCount = ((Number) req.get("nursingCount")).intValue();
                Integer userId = ((Number) req.get("userId")).intValue();
                boolean ok = nurseService.performNursing(customerId, itemId, nursingCount, userId);
                sendSuccess(exchange, Collections.singletonMap("success", ok));
            } else if ("DELETE".equals(method)) {
                Map<String, String> params = getQueryParams(exchange.getRequestURI().getQuery());
                Integer id = Integer.parseInt(params.get("id"));
                boolean ok = nurseService.deleteNurseRecord(id);
                sendSuccess(exchange, Collections.singletonMap("success", ok));
            } else {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
            }
        }
    }

    static class NurseRecordListHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            Map<String, String> params = getQueryParams(exchange.getRequestURI().getQuery());
            String name = params.get("name");
            List<Customer> customers = customerService.findCustomersByName(name);
            List<NurseRecord> allRecords = new ArrayList<>();
            for (Customer c : customers) {
                allRecords.addAll(nurseService.getNurseRecordsByCustomer(c.getId()));
            }
            CustomerDao cDao = new CustomerDao();
            UserDao uDao = new UserDao();
            List<Map<String, Object>> result = new ArrayList<>();
            for (NurseRecord r : allRecords) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", r.getId());
                map.put("nursingContent", r.getNursingContent());
                map.put("nursingCount", r.getNursingCount());
                map.put("nursingTime", r.getNursingTime());
                cDao.findById(r.getCustomerId()).ifPresent(c -> map.put("customerName", c.getCustomerName()));
                uDao.findById(r.getUserId()).ifPresent(u -> map.put("nickname", u.getNickname()));
                result.add(map);
            }
            sendSuccess(exchange, result);
        }
    }

    static class NurseRecordCustomerHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            Map<String, String> params = getQueryParams(exchange.getRequestURI().getQuery());
            Integer customerId = Integer.parseInt(params.get("customerId"));
            List<NurseRecord> records = nurseService.getNurseRecordsByCustomer(customerId);
            sendSuccess(exchange, records);
        }
    }

    // ---------- 健康管家 ----------
    static class HousekeeperListHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            List<User> list = housekeeperService.findAllHousekeepers();
            sendSuccess(exchange, list);
        }
    }

    static class AssignHousekeeperHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            Map<String, Object> req = gson.fromJson(readBody(exchange), new TypeToken<Map<String, Object>>() {}.getType());
            Integer customerId = ((Number) req.get("customerId")).intValue();
            Integer housekeeperId = ((Number) req.get("housekeeperId")).intValue();
            boolean ok = housekeeperService.assignHousekeeper(customerId, housekeeperId);
            sendSuccess(exchange, Collections.singletonMap("success", ok));
        }
    }

    static class HousekeeperCustomersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            Map<String, String> params = getQueryParams(exchange.getRequestURI().getQuery());
            Integer housekeeperId = Integer.parseInt(params.get("housekeeperId"));
            List<Customer> list = housekeeperService.findCustomersByHousekeeper(housekeeperId);
            sendSuccess(exchange, list);
        }
    }

    static class MyCustomersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            // 实际应从token获取userId，这里要求前端传递userId参数
            Map<String, String> params = getQueryParams(exchange.getRequestURI().getQuery());
            if (!params.containsKey("userId")) {
                sendResponse(exchange, 400, error(400, "缺少userId参数"));
                return;
            }
            Integer userId = Integer.parseInt(params.get("userId"));
            List<Customer> list = housekeeperService.findCustomersByHousekeeper(userId);
            sendSuccess(exchange, list);
        }
    }

    // ---------- 用户管理 ----------
    static class UserListHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            Map<String, String> params = getQueryParams(exchange.getRequestURI().getQuery());
            String name = params.get("name");
            List<User> list = userService.findUsersByName(name);
            sendSuccess(exchange, list);
        }
    }

    static class UserHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            if ("POST".equals(method)) {
                User user = gson.fromJson(readBody(exchange), User.class);
                User newUser = userService.addUser(user);
                sendSuccess(exchange, newUser);
            } else if ("PUT".equals(method)) {
                User user = gson.fromJson(readBody(exchange), User.class);
                boolean ok = userService.updateUser(user);
                sendSuccess(exchange, Collections.singletonMap("success", ok));
            } else if ("DELETE".equals(method)) {
                Map<String, String> params = getQueryParams(exchange.getRequestURI().getQuery());
                Integer id = Integer.parseInt(params.get("id"));
                boolean ok = userService.deleteUser(id);
                sendSuccess(exchange, Collections.singletonMap("success", ok));
            } else {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
            }
        }
    }

    static class ResetPasswordHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, error(405, "Method not allowed"));
                return;
            }
            Map<String, Object> req = gson.fromJson(readBody(exchange), new TypeToken<Map<String, Object>>() {}.getType());
            Integer id = ((Number) req.get("id")).intValue();
            boolean ok = userService.resetPassword(id);
            sendSuccess(exchange, Collections.singletonMap("success", ok));
        }
    }

    // ---------- 工具方法 ----------

    /**
     * 读取 HTTP 请求体为字符串
     *
     * @param exchange HTTP 交换对象
     * @return 请求体文本
     * @throws IOException 读取失败时抛出
     */

    private static String readBody(HttpExchange exchange) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            return br.lines().collect(Collectors.joining());
        }
    }

    /**
     * 解析 URL 查询字符串为键值对
     *
     * @param query 查询字符串（不含 ?）
     * @return 参数 Map，query 为 null 时返回空 Map
     */

    private static Map<String, String> getQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null) return params;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=");
            if (kv.length == 2) params.put(kv[0], kv[1]);
        }
        return params;
    }

    /**
     * 发送 JSON 响应并设置 CORS 头
     *
     * @param exchange   HTTP 交换对象
     * @param statusCode HTTP 状态码
     * @param response   响应 JSON 字符串
     * @throws IOException 写入响应失败时抛出
     */

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json;charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    /**
     * 发送成功响应（code=200，data 为业务数据）
     *
     * @param exchange HTTP 交换对象
     * @param data     业务数据对象
     * @throws IOException 写入响应失败时抛出
     */

    private static void sendSuccess(HttpExchange exchange, Object data) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", data);
        sendResponse(exchange, 200, gson.toJson(result));
    }

    /**
     * 发送错误响应
     *
     * @param exchange HTTP 交换对象
     * @param code     错误码
     * @param message  错误信息
     * @throws IOException 写入响应失败时抛出
     */

    private static void sendError(HttpExchange exchange, int code, String message) throws IOException {
        sendResponse(exchange, code, error(code, message));
    }

    /**
     * 构造标准错误 JSON 字符串
     *
     * @param code    错误码
     * @param message 错误信息
     * @return JSON 格式错误字符串
     */

    private static String error(int code, String message) {
        return String.format("{\"code\":%d,\"message\":\"%s\"}", code, message);
    }
}