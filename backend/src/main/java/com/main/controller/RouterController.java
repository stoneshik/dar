package com.main.controller;

import com.main.security.AuthorizeHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class RouterController {
    private final AuthorizeHandler authorizeHandler;
    /* Страницы для неавторизованного пользователя */
    @GetMapping("/")
    public String getHomeWithoutLoginPage(HttpServletRequest httpServletRequest) {
        final String login = authorizeHandler.getLoginBySessionId(httpServletRequest);
        if (!login.isEmpty()) {
            return "redirect:/main";
        }
        return "index";
    }
    @GetMapping("/login")
    public String getLoginPage() {
        return "index";
    }
    @GetMapping("/register")
    public String getRegisterPage() {
        return "index";
    }
    @GetMapping("/map_without_login")
    public String getMapWithoutLoginPage() {
        return "index";
    }
    /* Страницы для авторизованного пользователя */
    @GetMapping("/main")
    public String getHomePage() {
        return "index";
    }
    @GetMapping("/map")
    public String getMapPage() {
        return "index";
    }
    @GetMapping("/new_order_print")
    public String getNewOrderPrintPage() {
        return "index";
    }
    @GetMapping("/new_order_scan")
    public String getNewOrderScanPage() {
        return "index";
    }
    @GetMapping("/cart")
    public String getCartPage() {
        return "index";
    }
    @GetMapping("/files")
    public String getFilesPage() {
        return "index";
    }
    @GetMapping("/order_print/{id}")
    public String getOrderPrintPage(@PathVariable String id) {
        return "index";
    }
    @GetMapping("/order_scan/{id}")
    public String getOrderScanPage(@PathVariable String id) {
        return "index";
    }
    @GetMapping("/file/{id}")
    public String getFileByIdPage(@PathVariable String id) {
        return "index";
    }
    @GetMapping("/replenishes")
    public String getReplenishesPage() {
        return "index";
    }
    @GetMapping("/new_replenish")
    public String getNewReplenishPage() {
        return "index";
    }
}
