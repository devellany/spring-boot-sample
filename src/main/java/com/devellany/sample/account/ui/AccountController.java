package com.devellany.sample.account.ui;

import com.devellany.sample.account.application.AccountService;
import com.devellany.sample.account.domain.Account;
import com.devellany.sample.account.infra.exception.AlreadyConfirmTokenException;
import com.devellany.sample.account.infra.exception.UnknownEmailException;
import com.devellany.sample.account.ui.form.ChangeEmailForm;
import com.devellany.sample.account.ui.form.SignUpForm;
import com.devellany.sample.account.ui.params.EmailConfirmParams;
import com.devellany.sample.account.ui.validator.ChangeEmailFormValidator;
import com.devellany.sample.account.ui.validator.SignUpFormValidator;
import com.devellany.sample.common.infra.handler.CustomException;
import com.devellany.sample.common.infra.helper.AuthenticationHelper;
import com.devellany.sample.common.infra.ui.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RequestMapping("account")
@Controller
@RequiredArgsConstructor
public class AccountController extends BaseController {

    private final SignUpFormValidator signUpFormValidator;
    private final ChangeEmailFormValidator changeEmailFormValidator;
    private final AccountService accountService;

    @InitBinder("signUpForm")
    public void initSignUpFormBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @InitBinder("changeEmailForm")
    public void initChangeEmailBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(changeEmailFormValidator);
    }

    @GetMapping("sign-in")
    public String signInForm() {
        return "account/sign-in";
    }

    @GetMapping("sign-up")
    public String signUpFrom(Model model) {
        model.addAttribute(new SignUpForm());

        return "/account/sign-up";
    }

    @PostMapping("sign-up")
    public String signUpProcess(@Valid SignUpForm signUpForm, Errors errors,
                                RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            return "/account/sign-up";
        }

        accountService.processNewAccount(signUpForm);
        redirectAttributes.addAttribute("email", signUpForm.getEmail());
        return "redirect:/account/email/confirm";
    }

    @GetMapping("sign-out")
    public String SignOut(HttpServletRequest request, HttpServletResponse response) {
        AuthenticationHelper.SignOut(request, response);

        return "redirect:/";
    }

    @GetMapping("email/confirm")
    public String checkEmailToken(@Valid EmailConfirmParams emailConfirmParams, Model model, RedirectAttributes redirectAttributes,
                                  HttpServletRequest request, HttpServletResponse response) {
        try {
            Account account = accountService.processEmailConfirm(emailConfirmParams);

            AuthenticationHelper.signIn(account);
            model.addAttribute("nickname", account.getNickname());
        } catch (AlreadyConfirmTokenException e) {
            return "redirect:/";
        } catch (CustomException e) {
            AuthenticationHelper.SignOut(request, response);
            model.addAttribute("email", emailConfirmParams.getEmail());
            model.addAttribute("error", e.getExceptionName());
        }

        redirectAttributes.addAttribute("email", emailConfirmParams.getEmail());
        return "/account/email/confirm";
    }

    @GetMapping("email/resend")
    public String resendEmail(@Valid EmailConfirmParams emailConfirmParams, RedirectAttributes redirectAttributes) {
        try {
            accountService.resendEmailForConfirm(emailConfirmParams);
        } catch (UnknownEmailException e) {
            return "/account/email/fail-resend";
        }

        redirectAttributes.addAttribute("email", emailConfirmParams.getEmail());
        return "redirect:/account/email/confirm";
    }

    @GetMapping("email/change")
    public String changePasswordForm(Model model) {
        model.addAttribute(new ChangeEmailForm());

        return "/account/email/change";
    }

    @PostMapping("email/change")
    public String changePasswordProcess(@Valid ChangeEmailForm changeEmailForm, Errors errors,
                                RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            return "/account/email/change";
        }

        accountService.processChangeEmail(changeEmailForm);
        redirectAttributes.addAttribute("email", changeEmailForm.getChangeEmail());
        return "redirect:/account/email/confirm";
    }
}
