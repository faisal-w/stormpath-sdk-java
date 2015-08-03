/*
 * Copyright 2015 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.spring.boot.autoconfigure

import autoconfigure.BeanOverrideApplication
import com.stormpath.sdk.impl.cache.DisabledCacheManager
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager
import com.stormpath.sdk.servlet.event.RequestEventListener
import com.stormpath.sdk.servlet.filter.account.AuthenticationJwtFactory
import com.stormpath.sdk.servlet.filter.account.JwtSigningKeyResolver
import com.stormpath.sdk.servlet.http.authc.HeaderAuthenticator
import com.stormpath.sdk.servlet.mvc.Controller
import com.stormpath.spring.security.provider.AccountCustomDataPermissionResolver
import com.stormpath.spring.security.provider.GroupPermissionResolver
import com.stormpath.spring.security.provider.StormpathAuthenticationProvider
import com.stormpath.spring.security.provider.UsernamePasswordAuthenticationTokenFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.context.web.WebAppConfiguration
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * @since 1.0.RC4.6
 */
@SpringApplicationConfiguration(classes = BeanOverrideApplication.class)
@WebAppConfiguration
class BeanOverrideApplicationIT extends AbstractTestNGSpringContextTests {

    static{
        //The groupPermissionResolver bean is always being automatically created (defined in BeanOverrideApplication).
        //Let's create it conditionally on this property to avoid StormpathSpringSecurityWebMvcAutoConfigurationIT to fail
        System.setProperty("testName", "BeanOverrideApplicationIT")
    }

    //Spring Security Beans
    @Autowired
    StormpathAuthenticationProvider stormpathAuthenticationProvider

    @Autowired
    public GroupPermissionResolver groupPermissionResolver;

    //Some WebMVC Beans
    @Autowired
    AuthenticationJwtFactory stormpathAuthenticationJwtFactory

    @Autowired
    JwtSigningKeyResolver stormpathJwtSigningKeyResolver

    @Autowired
    RequestEventListener stormpathRequestEventListener

    @Autowired
    CsrfTokenManager stormpathCsrfTokenManager

    @Autowired
    HeaderAuthenticator stormpathAuthorizationHeaderAuthenticator

    @Autowired
    Controller stormpathForgotPasswordController

    @Test
    void test() {

        assertNotNull stormpathAuthenticationProvider
        assertEquals stormpathAuthenticationProvider.applicationRestUrl, "https://api.stormpath.com/v1/applications/2RNMCGQeePR0yREsG5foC0"
        assertNotNull stormpathAuthenticationProvider.client

        assertTrue stormpathAuthenticationProvider.client.dataStore.cacheManager instanceof DisabledCacheManager
        assertTrue stormpathAuthenticationProvider.groupGrantedAuthorityResolver instanceof CustomTestGroupGrantedAuthorityResolver
        assertTrue groupPermissionResolver instanceof CustomTestGroupPermissionResolver
        assertTrue stormpathAuthenticationProvider.groupPermissionResolver instanceof CustomTestGroupPermissionResolver
        assertNull stormpathAuthenticationProvider.accountGrantedAuthorityResolver
        assertTrue stormpathAuthenticationProvider.accountPermissionResolver instanceof AccountCustomDataPermissionResolver
        assertTrue stormpathAuthenticationProvider.authenticationTokenFactory instanceof UsernamePasswordAuthenticationTokenFactory

        //Some WebMVC beans
        assertNotNull stormpathAuthenticationJwtFactory
        assertNotNull stormpathJwtSigningKeyResolver
        assertNotNull stormpathRequestEventListener
        assertNotNull stormpathCsrfTokenManager
        assertNotNull stormpathAuthorizationHeaderAuthenticator
        assertNotNull stormpathForgotPasswordController
    }

}
