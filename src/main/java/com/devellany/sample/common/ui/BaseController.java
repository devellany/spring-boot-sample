package com.devellany.sample.common.ui;

import com.devellany.sample.common.infra.config.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

public abstract class BaseController {
    @Autowired protected AppProperties appProperties;

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("app", appProperties);
    }
}
