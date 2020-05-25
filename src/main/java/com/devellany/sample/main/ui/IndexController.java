package com.devellany.sample.main.ui;

import com.devellany.sample.account.domain.Account;
import com.devellany.sample.account.infra.CurrentAccount;
import com.devellany.sample.common.ui.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController  extends BaseController {

    @GetMapping("/")
    public String index(@CurrentAccount Account account, Model model) {
        if (account == null) {
            return "account/sign-in";
        }

        model.addAttribute("account", account);
        return "main/index";
    }
}