import java.util.Scanner;

public class Bank {
    public static void main(String[] args) {
        // 初始化账户信息
        String user = "admin";// 账户
        String pwd = "123";// 密码
        double money = 10000;// 余额
        Scanner sc = new Scanner(System.in);
        // 登陆界面
        welcome();
        // 用户密码匹配方法
        boolean isRight = longin(user, pwd);
        while (isRight) {
            // 登陆成功，进入操作界面
            while (true) {
                System.out.println("1.查询余额，2.存款，3.取款，4.修改密码，5.退出");
                int opt = sc.nextInt();
                switch (opt) {
                    case 1:
                        // 查询余额
                        checkMoney(money);
                        decide();
                        break;
                    case 2:
                        // 存款
                        money += setMoney(money);
                        decide();
                        break;
                    case 3:
                        // 取款
                        money -= getMoney(money);
                        decide();
                        break;
                    case 4:
                        // 修改密码,因为无数据库，直接打印当前密码查看是否修改成功
                        String str = changePwd(pwd);
                        System.out.println("当前密码为" + str);
                        decide();
                        break;
                    case 5:
                        // 退出
                        System.out.println("退出成功");
                        System.exit(0);
                    default:
                        System.out.println("非法操作");
                        break;
                }
            }
        }
    }

    /**
     * 欢迎界面
     */
    public static void welcome() {
        // 欢迎界面
        System.out.println("------------------------------");
        System.out.println(" ******    欢迎登陆      *******");
        System.out.println(" ******      银行   *******");
        System.out.println(" *****     Vesion1.0****");
        System.out.println("-------------------------------");
    }

    /**
     * 创建一个方法，判断账户密码是否正确
     *
     * @param user
     * @param pwd
     */
    public static boolean longin(String user, String pwd) {
        // 账户密码3次匹配机会，错误则吞卡
        Scanner sc = new Scanner(System.in);
        for (int i = 3; i > 0; i--) {
            System.out.println("请输入账号");
            String new_user = sc.next();
            System.out.println("请输密码");
            String new_pwd = sc.next();
            if (user.equals(new_user) && pwd.equals(new_pwd)) {
                // 登陆成功
                System.out.println("登陆成功");
                return true;
            } else {
                if (i - 1 == 0) {
                    System.out.println("密码错误3次，吞卡");
                    System.exit(0);
                }
                System.out.println("账户名或密码错误，请重新输入今日还剩余" + (i - 1) + "次机会");
            }
        }
        return false;
    }

    /**
     * 判断是否继续操作 y/n
     *
     * @param
     */
    public static void decide() {
        Scanner sc = new Scanner(System.in);
        System.out.println("是否继续操作（y/n）");
        String dec = sc.next();
        if (dec.equals("y")) {
            return;
        } else if (dec.equals("n")) {
            System.out.println("退出成功");
            System.exit(0);
        } else {
            while (true) {
                System.out.println("输入错误，请重新输入 是否继续（y/n）");
                String temp = sc.next();
                if (temp.equals("y")) {
                    return;
                } else if (temp.equals("n")) {
                    System.out.println("退出成功");
                    System.exit(0);
                }
            }
        }
    }

    /**
     * 查询余额
     *
     * @param money
     */
    public static void checkMoney(double money) {
        System.out.println("您的余额为" + money);
    }

    /**
     * 创建一个方法 存款
     *
     * @param money
     * @return 存款金额
     */
    public static double setMoney(double money) {
        System.out.println("请输入您要存款的金额");
        Scanner sc = new Scanner(System.in);
        double addMoney = sc.nextInt();
        if (addMoney > 10000) {
            System.out.println("单次取款金额超过1万元");
            return 0;
        } else if (addMoney < 0) {
            System.out.println("存款金额错误");
            return 0;
        } else if (addMoney % 100 != 0) {
            System.out.println("请输入100的整数倍存款");
            return 0;
        } else {
            System.out.println("存款成功，当前余额为" + (money + addMoney));
            return addMoney;
        }
    }

    /**
     * 创建一个方法 取款
     *
     * @param money
     * @return 取出金额
     */
    public static double getMoney(double money) {
        System.out.println("请输入您要取款的金额");
        Scanner sc = new Scanner(System.in);
        double subMoney = sc.nextInt();
        if (subMoney > money) {
            System.out.println("余额不足");
            return 0;
        } else if (subMoney > 3000) {
            System.out.println("单次取款金额超过3000");
            return 0;
        } else if (subMoney < 0) {
            System.out.println("取款金额错误");
            return 0;
        } else if (subMoney % 100 != 0) {
            System.out.println("请取出100的整数倍存款");
            return 0;
        } else {
            System.out.println("取款成功，当前余额为" + (money - subMoney));
            return subMoney;
        }
    }

    /**
     * 创建一个方法 修改密码
     *
     * @param pwd
     * @return 修改成功返回新密码，失败返回旧密码
     */
    public static String changePwd(String pwd) {
        System.out.println("请输入旧密码");
        Scanner sc = new Scanner(System.in);
        String old_pwd = sc.next();
        if (old_pwd.equals(pwd)) {
            System.out.println("请输入新密码");
            String new_pwd = sc.next();
            System.out.println("请再次输入新密码");
            String new_pwd1 = sc.next();
            if (new_pwd.equals(new_pwd1)) {
                System.out.println("密码修改成功");
                return new_pwd;
            } else {
                System.out.println("两次输入密码不一致，请重新输入");
                return pwd;
            }

        } else {
            System.out.println("旧密码输入错误");
        }
        return pwd;
    }
}
