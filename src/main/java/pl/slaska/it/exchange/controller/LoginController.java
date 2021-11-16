package pl.slaska.it.exchange.controller;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import pl.slaska.it.exchange.dao.UserDAO;
import pl.slaska.it.exchange.dao.UserDao;
import pl.slaska.it.exchange.model.UserDetails;

class UserValidator implements Validator {
    @Override
    public boolean supports(Class<?> cls) {
        return UserDetails.class.isAssignableFrom(cls);
    }
    @Override
    public void validate(Object obj, Errors errors) {
        UserDetails userDetails = (UserDetails)obj;
        if (userDetails.getPassword().trim().equals(""))
            errors.rejectValue("password", "obligatori",
                    "El campo contraseña no puede estar vacio");
    }
}

@Controller
public class LoginController {

    @Autowired
    private UserDao userDao;
    private UserDAO userDAO;

    @Autowired
    public void setCiudadanoDAO(UserDAO userDAO) { this.userDAO = userDAO; }


    /**
     * LOGIN DEL USUARIO
     */

    @RequestMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new UserDetails());
        return "login";
    }

    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String checkLogin(@ModelAttribute("user") UserDetails user,
                             BindingResult bindingResult, HttpSession session) {
        UserValidator userValidator = new UserValidator();
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "login";
        }
        user = userDao.loadUserByUsername(user.getEmail(), user.getPassword(), userDAO);
        if (user == null) {
            bindingResult.rejectValue("password", "password", "Contraseña incorrecta");
            return "login";
        }
        session.setAttribute("user", user);
        return "redirect:/user/ciudadano";
    }


    /**
     * CERRAR SESION
     */

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/user/ciudadano";
    }
}