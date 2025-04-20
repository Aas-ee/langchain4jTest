package com.aasee.langchain4jtest;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v120.runtime.Runtime;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QQMusicQRCodeFetcher {

    public static void main(String[] args) {
        // 自动配置并下载 ChromeDriver
        WebDriverManager.chromedriver().setup();

        // 无头模式配置
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless=new"); // 新版本 Chrome 用 new 模式
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                + "(KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");

        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches", new String[]{ "enable-automation" });
        options.addArguments("--disable-blink-features=AutomationControlled");
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

        try {
            // 1. 打开 QQ 音乐首页
            driver.get("https://y.qq.com/");

            // 2. 等待并点击登录按钮
            WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='app']/div/div[1]/div/div[2]/span/a")));
            loginBtn.click();

            //  等待 iframe 出现（注意是 login_frame）
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("login_frame")));

            List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
            System.out.println("Number of iframes: " + iframes.size());
            for (WebElement iframe : iframes) {
                System.out.println("iframe id: " + iframe.getAttribute("id") +
                        ", src: " + iframe.getAttribute("src"));
            }

            //  等待 iframe 出现（注意是 ptlogin_iframe，不是 login_frame）
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("ptlogin_iframe")));

            //  等待二维码 img 加载出来
            WebElement qrImg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("qrlogin_img")));

//            // 4. 完成操作后需要两次切换回主文档
//            driver.switchTo().parentFrame(); // 回到 login_frame
//            driver.switchTo().defaultContent(); // 回到主文档
//
//            // 6. 获取 login_frame 元素的位置和尺寸
//            WebElement loginFrame = driver.findElement(By.id("login_frame"));
//            Point location = loginFrame.getLocation();
//            Dimension size = loginFrame.getSize();
//
//            // 7. 截取整个页面的截图
//            TakesScreenshot screenshotDriver = (TakesScreenshot) driver;
//            File screenshot = screenshotDriver.getScreenshotAs(OutputType.FILE);
//
//            // 将截图文件加载为图像
//            BufferedImage fullScreenshot = ImageIO.read(screenshot);
//
//            // 8. 裁剪出 login_frame 部分
//            BufferedImage loginFrameScreenshot = fullScreenshot.getSubimage(location.getX(), location.getY(),
//                    size.getWidth(), size.getHeight());

            // 3. 直接截取元素（无需坐标计算）
            File qrCodeFile = qrImg.getScreenshotAs(OutputType.FILE);
            BufferedImage qrImage = ImageIO.read(qrCodeFile);

            // 9. 保存裁剪后的截图
            File outputfile = new File("login_frame_screenshot.png");
            ImageIO.write(qrImage, "png", outputfile);
            System.out.println("login_frame 截图已保存为 login_frame_screenshot.png");

            driver.switchTo().parentFrame(); // 回到 login_frame
            driver.switchTo().defaultContent(); // 回到主文档

            // 6. 在手动扫描二维码后等待登录成功
            // 使用提供的头像 XPath 确认登录成功
            WebElement userAvatar = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='app']/div/div[1]/div/div[2]/span/a/img[1]")));


            if (userAvatar.isDisplayed()) {
                System.out.println("登录成功！");

                // 7. 获取登录后的 Cookie
                Set<Cookie> cookies = driver.manage().getCookies();
                for (Cookie cookie : cookies) {
                    System.out.println("Cookie: " + cookie.getName() + " = " + cookie.getValue());
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}
