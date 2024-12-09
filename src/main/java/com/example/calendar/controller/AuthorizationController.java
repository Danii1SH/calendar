package com.example.calendar.controller;


import com.example.calendar.model.User;
import com.example.calendar.repository.UsersRepository;
import com.example.calendar.service.HashEncoder;
import com.example.calendar.service.IEncoder;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/authorization")
public class AuthorizationController {

    private final UsersRepository db;
    private final IEncoder encoder;
    private final int timeLifeInSeconds;

    public AuthorizationController(UsersRepository db) {
        this.db = db;
        this.encoder = new HashEncoder();
        timeLifeInSeconds = 86400;
    }

    private boolean validCheck(User user, User userForDB) {
        return userForDB != null && encoder.checkPassword(userForDB.getPassword(), user.getPassword());
    }

    @GetMapping("/")
    public String authorization(@CookieValue(value = "loggedInUser", defaultValue = "") String loggedInUser,
                                @ModelAttribute("error") String error, Model model,
                                RedirectAttributes redirectAttributes) {
        if (!loggedInUser.isEmpty()) {
            User userForDb = db.getUserByLogin(loggedInUser);
            if (userForDb != null) {
                redirectAttributes.addFlashAttribute("currentUser", userForDb);
                return "redirect:/personalAccount/";
            }
        }
        model.addAttribute("authUser", new User());
        return "authorization";
    }

    @PostMapping("/authentification")
    public String authentification(@ModelAttribute("authUser") User checkUser,
                                   RedirectAttributes redirectAttributes, Model model) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

        User userForDb = db.getUserByLogin(checkUser.getLogin());

        if (!validCheck(checkUser, userForDb)) {
            String error = "Не верный логин/пароль";
            model.addAttribute("error", error);
            return "authorization";
        }


        Cookie cookie = new Cookie("loggedInUser", userForDb.getLogin());
        cookie.setMaxAge(timeLifeInSeconds);
        cookie.setPath("/");
        response.addCookie(cookie);

        redirectAttributes.addFlashAttribute("currentUser", userForDb);
        return "redirect:/personalAccount/";
    }
}
