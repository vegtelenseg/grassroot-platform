package za.org.grassroot.webapp.controller.ussd;

import com.google.common.collect.Sets;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import za.org.grassroot.core.domain.BaseRoles;
import za.org.grassroot.core.domain.User;
import za.org.grassroot.services.*;
import za.org.grassroot.services.async.AsyncUserLogger;
import za.org.grassroot.services.util.CacheUtilService;
import za.org.grassroot.webapp.util.USSDGroupUtil;

import java.util.Properties;
import java.util.Set;

/**
 * Created by luke on 2015/11/20.
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class USSDAbstractUnitTest {

    protected MockMvc mockMvc;

    @Mock
    protected UserManagementService userManagementServiceMock;

    @Mock
    protected GroupBroker groupBrokerMock;

    @Mock
    protected EventManagementService eventManagementServiceMock;

    @Mock
    protected EventLogManagementService eventLogManagementServiceMock;

    @Mock
    protected PermissionBroker permissionBrokerMock;

    @Mock
    protected EventBroker eventBrokerMock;

    @Mock
    protected EventRequestBroker eventRequestBrokerMock;

    @Mock
    protected LogBookBroker logBookBrokerMock;

    @Mock
    protected LogBookRequestBroker logBookRequestBrokerMock;

    @Mock
    protected CacheUtilService cacheUtilManagerMock;

    @Mock
    protected AsyncUserLogger asyncUserLoggerMock;

    @InjectMocks
    protected USSDGroupUtil ussdGroupUtil;

    protected static final String base = "/ussd/";
    protected static final String userChoiceParam = "request";
    protected static final String interruptedChoice = "1";

    protected HandlerExceptionResolver exceptionResolver() {
        SimpleMappingExceptionResolver exceptionResolver = new SimpleMappingExceptionResolver();
        Properties statusCodes = new Properties();

        statusCodes.put("error/404", "404");
        statusCodes.put("error/error", "500");

        exceptionResolver.setStatusCodes(statusCodes);
        return exceptionResolver;
    }

    protected LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    protected ViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();

        // viewResolver.setViewClass
        viewResolver.setPrefix("/pages");
        viewResolver.setSuffix(".html");

        return viewResolver;
    }

    protected MessageSource messageSource() {
        // todo: wire this up to the actual message source so unit tests make sure languages don't have gaps ...
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

        messageSource.setBasename("messages");
        messageSource.setUseCodeAsDefaultMessage(true);

        return messageSource;
    }

    protected void wireUpMessageSourceAndGroupUtil(USSDController controller, USSDGroupUtil groupUtil) {
        controller.setMessageSource(messageSource());
        groupUtil.setMessageSource(messageSource());
        controller.setUssdGroupUtil(groupUtil);
    }

    // helper method to generate a set of membership info ... used often
    protected Set<MembershipInfo> ordinaryMember(String phoneNumber) {
        return Sets.newHashSet(new MembershipInfo(phoneNumber, BaseRoles.ROLE_ORDINARY_MEMBER, null));
    }

    protected Set<MembershipInfo> organizer(User user) {
        return Sets.newHashSet(new MembershipInfo(user.getPhoneNumber(), BaseRoles.ROLE_GROUP_ORGANIZER, user.getDisplayName()));
    }
}
