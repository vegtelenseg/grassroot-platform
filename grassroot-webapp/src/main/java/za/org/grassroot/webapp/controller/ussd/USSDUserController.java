package za.org.grassroot.webapp.controller.ussd;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import za.org.grassroot.core.domain.User;
import za.org.grassroot.webapp.controller.ussd.menus.USSDMenu;
import za.org.grassroot.webapp.enums.USSDSection;
import za.org.grassroot.webapp.model.ussd.AAT.Request;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author luke on 2015/08/14.
 */

@RequestMapping(method = GET, produces = MediaType.APPLICATION_XML_VALUE)
@RestController
public class USSDUserController extends USSDController {

    private static final String keyStart = "start", keyName = "name";
    private static final String keyLanguage = "language", keyPhone = "phone";
    private static final USSDSection thisSection = USSDSection.USER_PROFILE;

    /**
     * Starting the user profile management flow here
     */

    @RequestMapping(value = homePath + userMenus + startMenu)
    @ResponseBody
    public Request userProfile(@RequestParam(value= phoneNumber, required=true) String inputNumber) throws URISyntaxException {

        User sessionUser = userManager.findByInputNumber(inputNumber);

        USSDMenu thisMenu = new USSDMenu(getMessage(thisSection, startMenu, promptKey, sessionUser));

        thisMenu.addMenuOption(userMenus + keyName, getMessage(thisSection, startMenu, optionsKey + keyName, sessionUser));
        thisMenu.addMenuOption(userMenus + keyLanguage, getMessage(thisSection, startMenu, optionsKey + keyLanguage, sessionUser));
       // thisMenu.addMenuOption(userMenus + keyPhone, getMessage(thisSection, startMenu, optionsKey + keyPhone, sessionUser));

        return menuBuilder(thisMenu);
    }

    @RequestMapping(value = homePath + userMenus + keyName)
    @ResponseBody
    public Request userDisplayName(@RequestParam(value= phoneNumber, required=true) String inputNumber) throws URISyntaxException {

        USSDMenu thisMenu = new USSDMenu("", userMenus + keyName + doSuffix);

        User sessionUser;
        try { sessionUser = userManager.findByInputNumber(inputNumber); }
        catch (NoSuchElementException e) { return noUserError; }

        if (sessionUser.hasName()) {
            thisMenu.setPromptMessage(getMessage(thisSection, keyName, promptKey + ".named", sessionUser.getDisplayName(), sessionUser));
        } else {
            thisMenu.setPromptMessage(getMessage(thisSection, keyName, promptKey + ".unnamed", sessionUser));
        }

        return menuBuilder(thisMenu);
    }

    @RequestMapping(value = homePath + userMenus + keyName + doSuffix)
    @ResponseBody
    public Request userChangeName(@RequestParam(value= phoneNumber, required=true) String inputNumber,
                                  @RequestParam(value= userInputParam, required=true) String newName) throws URISyntaxException {

        // todo: add validation and processing of the name that is passed, as well as exception handling etc

        User sessionUser;
        try { sessionUser = userManager.findByInputNumber(inputNumber); }
        catch (NoSuchElementException e) { return noUserError; }

        sessionUser.setDisplayName(newName);
        sessionUser = userManager.save(sessionUser);

        return menuBuilder(new USSDMenu(getMessage(thisSection, keyName + doSuffix, promptKey, sessionUser), optionsHomeExit(sessionUser)));
    }

    @RequestMapping(value = homePath + userMenus + keyLanguage)
    @ResponseBody
    public Request userPromptLanguage(@RequestParam(value= phoneNumber, required=true) String inputNumber) throws URISyntaxException {

        User sessionUser;
        try { sessionUser = userManager.findByInputNumber(inputNumber); }
        catch (NoSuchElementException e) { return noUserError; }

        USSDMenu thisMenu = new USSDMenu(getMessage(thisSection, keyLanguage, promptKey, sessionUser));

        for (Map.Entry<String, String> entry : userManager.getImplementedLanguages().entrySet()) {
            thisMenu.addMenuOption(userMenus + keyLanguage + doSuffix + "?language=" + entry.getKey(), entry.getValue());
        }

        return menuBuilder(thisMenu);
    }

    @RequestMapping(value = homePath + userMenus + keyLanguage + doSuffix)
    @ResponseBody
    public Request userChangeLanguage(@RequestParam(value= phoneNumber, required=true) String inputNumber,
                                      @RequestParam(value="language", required=true) String language) throws URISyntaxException {

        User sessionUser;
        try { sessionUser = userManager.findByInputNumber(inputNumber); }
        catch (NoSuchElementException e) { return noUserError; }

        sessionUser.setLanguageCode(language);
        sessionUser = userManager.save(sessionUser);

        return menuBuilder(new USSDMenu(getMessage(thisSection, keyLanguage + doSuffix, promptKey, sessionUser),
                                        optionsHomeExit(sessionUser)));
    }

}