# 东软颐养中心管理系统

东北大学软件学院基础编程实训项目——面向养老机构的 **Java Swing 桌面端和 vue web端双UI尝试** 信息管理系统，用于客户入住、床位、护理、膳食及健康管家日常业务的管理。

## 功能概览

系统按用户角色分为 **管理员** 与 **健康管家** 两套界面。

### 管理员端（`AdminFrame`）

| 模块 | 主要功能 |
|------|----------|
| 客户管理 | 入住登记、信息修改/删除、外出/退住申请审核、按姓名与老人类型查询 |
| 床位管理 | 床位与房间维护、换床、床位使用情况查询 |
| 护理管理 | 护理项目、护理级别、客户护理方案配置 |
| 健康管家管理 | 为客户分配/解除健康管家 |
| 用户管理 | 健康管家账号的增删改、密码重置（不可操作系统管理员） |
| 膳食管理 | 食品库、客户饮食喜好、膳食日历 |
| 统计信息 | 床位/客户/护理记录等数据概览与明细 |

### 健康管家端（`HousekeeperFrame`）

| 模块 | 主要功能 |
|------|----------|
| 我的客户 | 查看已分配客户 |
| 日常护理 | 按护理项目执行日常护理 |
| 护理记录 | 查看与登记护理记录 |
| 外出申请 | 提交客户外出申请 |
| 退住申请 | 提交客户退住申请 |
| 我的申请 | 外出/退住申请列表及回院登记 |

## 技术栈

| 类别 | 技术 |
|------|------|
| 语言 | Java |
| 界面 | Swing + [FlatLaf](https://github.com/JFormDesigner/FlatLaf) 3.7.1 |
| 数据库 | MySQL 8.x |
| 数据访问 | JDBC，自研泛型 `BaseDao` / `BaseDaoImpl` |
| 架构 | 三层：View → Service → DAO → Database |

## 项目结构

```
BasicProgramTraining/
├── src/
│   ├── view/           # 界面：Launcher、登录/主窗口、Panel、Dialog、主题与工具类
│   ├── service/        # 业务逻辑（8 个 Service）
│   ├── dao/            # 数据访问（BaseDao、DBUtil、18 个具体 Dao）
│   ├── pojo/           # 实体类（与数据库表对应）
│   └── util/           # 通用工具（如 DateUtils）
├── lib/                # 第三方 JAR（如 flatlaf-3.7.1.jar）
├── docs/uml/           # PlantUML 架构与领域模型图
├── neusoft.iml         # IntelliJ 模块配置
└── README.md
```

### 分层说明

```
view.Launcher
    └── LoginFrame → AuthService
            ├── roleId=1 → AdminFrame
            └── roleId=2 → HousekeeperFrame
                    └── Panel / Dialog
                            └── Service
                                    └── Dao (extends BaseDaoImpl)
                                            └── DBUtil → MySQL (neu)
```

更详细的 UML 见 [`docs/uml/README.md`](docs/uml/README.md)。

## 环境要求

- **JDK**：8 及以上（推荐 21+）
- **MySQL**：8.0+，已创建数据库 `neu` 及对应业务表
- **IDE**：IntelliJ IDEA（推荐）或 Eclipse / VS Code
- **node.js**：v24.x或更新版本
- **依赖 JAR**（放入 `lib/` 并在 IDE 中添加到 Module Library）：
  - `flatlaf-3.7.1.jar`（已包含）
  - `mysql-connector-j-*.jar`（MySQL JDBC 驱动，需自行添加）

## 快速开始

### 1. 克隆项目

```bash
git clone <仓库地址>
cd BasicProgramTraining
```

### 2. 准备数据库

1. 安装并启动 MySQL。
2. 创建数据库（库名需与配置一致，默认为 `neu`）：

```sql
CREATE DATABASE IF NOT EXISTS neu DEFAULT CHARACTER SET utf8mb4;
```

3. 导入课程提供的建表 SQL 脚本（表名需与 Dao 中一致，例如 `user`、`customer`、`bed`、`room` 等）。
4. 修改数据库连接：编辑 `src/dao/DBUtil.java` 中的 `URL`、`USER`、`PASSWORD`。

```java
private static final String URL = "jdbc:mysql://localhost:3306/neu?useSSL=false&serverTimezone=UTC";
private static final String USER = "root";
private static final String PASSWORD = "你的密码";
```

> 也可使用 `src/dao/TestConnection.java` 单独测试数据库连通性。

### 3. 配置 IDE

**IntelliJ IDEA**

1. `File` → `Open` 打开项目根目录。
2. 确认 `Project Structure` → `Libraries` 中已引用 `lib` 目录（含 FlatLaf 与 MySQL 驱动）。
3. 将 `src` 标记为 **Sources Root**。
4. 运行主类：`view.Launcher`。

**命令行编译运行（示例）**

```bash
# Windows，请将路径与 jar 文件名按实际调整
javac -encoding UTF-8 -cp "lib/*" -d out src/dao/*.java src/pojo/*.java src/service/*.java src/util/*.java src/view/**/*.java
java -cp "out;lib/*" view.Launcher
```

### 4. 登录系统

首次启动时，`UserService.initAdmins()` 会自动创建默认管理员（若用户名不存在）：

| 用户名 | 默认密码 | 角色 |
|--------|----------|------|
| admin | admin | 管理员 (roleId=1) |
| admin1 | admin1 | 管理员 |
| admin2 | admin2 | 管理员 |

- **管理员**（`roleId = 1`）→ 进入管理员主界面  
- **健康管家**（`roleId = 2`）→ 进入健康管家主界面  

健康管家账号由管理员在「用户管理」中创建；新建用户默认密码为 **手机号后 6 位**，无手机号时为 `123456`。

## 核心业务类

| 包 | 说明 |
|----|------|
| `view.Launcher` | 程序入口，初始化主题与默认管理员 |
| `service.AuthService` | 登录校验 |
| `service.CustomerService` | 客户、外出、退住等业务 |
| `service.BedService` | 床位、房间、换床 |
| `service.NurseService` | 护理项目、级别、记录 |
| `service.MealService` | 膳食与饮食喜好 |
| `service.HousekeeperService` | 健康管家与客户分配 |
| `service.StatisticsService` | 统计数据 |
| `dao.BaseDaoImpl` | 通用 CRUD（反射映射、逻辑删除） |

## 数据访问说明

- 各实体 Dao 继承 `BaseDaoImpl<实体, Integer>`，表名与字段采用 **驼峰 ↔ 下划线** 自动映射。
- 支持 `is_deleted` 字段的 **逻辑删除**（`deleteById`）。
- 部分 Dao 提供自定义 SQL 方法（如 `UserDao.findByUsername`）。

本地可运行 `dao.DAOTest` 对各 Dao 进行联调测试（会向数据库写入测试数据，请在测试库使用）。

## 界面与主题

- 使用 **FlatLightLaf** 浅色主题，全局字体为「微软雅黑」14px。
- 主题与表格样式见 `view.theme.UITheme`、`view.util.UIUtils`、`view.util.TableUtils`。

## 常见问题

| 问题 | 处理建议 |
|------|----------|
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | 将 MySQL Connector/J 加入 `lib/` 并配置到 classpath |
| 无法连接数据库 | 检查 MySQL 服务、库名 `neu`、账号密码及 `DBUtil` 配置（注意将password更换为自己的） |
| 登录后提示「未知角色」 | 确认用户 `role_id` 为 1 或 2 |
| 中文乱码 | 编译时指定 `-encoding UTF-8`，数据库使用 `utf8mb4` |
| 表不存在 / SQL 报错 | 确认已执行建表脚本，表名与 Dao 构造函数中一致 |

## 文档与图示

- UML 图（架构、领域模型、时序图等）：[`docs/uml/`](docs/uml/)
- 使用 PlantUML 插件或在线工具打开 `.puml` 文件预览

## 参与贡献

1. Fork 本仓库  
2. 新建功能分支（如 `feat/xxx`）  
3. 提交代码并发起 Pull Request  

## 许可证

本项目为东北大学软件学院课程实训作业，仅供学习交流使用。
