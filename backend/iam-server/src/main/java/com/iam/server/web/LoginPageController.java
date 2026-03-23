package com.iam.server.web;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginPageController {
  @GetMapping(value = "/login", produces = MediaType.TEXT_HTML_VALUE)
  @ResponseBody
  public byte[] loginPage(@RequestParam(value = "error", required = false) final String error,
                          @RequestParam(value = "logout", required = false) final String logout,
                          final HttpServletRequest request) {
    CsrfToken csrf = (CsrfToken) request.getAttribute("_csrf");
    String csrfInput = "";
    if (csrf != null) {
      csrfInput =
          "<input name=\"" + escape(csrf.getParameterName()) + "\" type=\"hidden\" value=\"" + escape(csrf.getToken()) + "\" />";
    }

    String banner = "";
    if (error != null) {
      banner = "<div class=\"iam-alert iam-alert--error\">用户名或密码错误</div>";
    } else if (logout != null) {
      banner = "<div class=\"iam-alert iam-alert--info\">已退出登录</div>";
    }

    String html =
        "<!DOCTYPE html>" +
            "<html lang=\"zh-CN\">" +
            "<head>" +
            "<meta charset=\"utf-8\" />" +
            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />" +
            "<title>IAM 登录</title>" +
            "<link rel=\"stylesheet\" href=\"/login.css\" />" +
            "</head>" +
            "<body>" +
            "<main class=\"iam-page\">" +
            "<section class=\"iam-card\">" +
            "<div class=\"iam-brand\">" +
            "<div class=\"iam-brand__badge\">IAM</div>" +
            "<div class=\"iam-brand__text\">" +
            "<div class=\"iam-brand__title\">欢迎登录</div>" +
            "<div class=\"iam-brand__sub\">Authorization Server</div>" +
            "</div>" +
            "</div>" +
            banner +
            "<form class=\"iam-form\" method=\"post\" action=\"/login\">" +
            "<label class=\"iam-label\" for=\"username\">用户名</label>" +
            "<input class=\"iam-input\" type=\"text\" id=\"username\" name=\"username\" \n" +
            "placeholder=\"请输入用户名\" autocomplete=\"username\" required autofocus />" +
            "<label class=\"iam-label\" for=\"password\">密码</label>" +
            "<input class=\"iam-input\" type=\"password\" id=\"password\" name=\"password\" \n" +
            "placeholder=\"请输入密码\" autocomplete=\"current-password\" required />" +
            csrfInput +
            "<button class=\"iam-button\" type=\"submit\">登录</button>" +
            "</form>" +
            "<div class=\"iam-foot\">默认账号：<span class=\"iam-mono\">admin / Admin@123</span></div>" +
            "</section>" +
            "</main>" +
            "</body>" +
            "</html>";

    return html.getBytes(StandardCharsets.UTF_8);
  }

  private static String escape(final String v) {
    return v == null ? "" : v
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;");
  }
}

