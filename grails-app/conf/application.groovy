grails.gorm.default.mapping = {
    id generator: 'identity'
}

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'top.dteam.earth.backend.user.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'top.dteam.earth.backend.user.UserRole'
grails.plugin.springsecurity.authority.className = 'top.dteam.earth.backend.user.Role'

// 监听登录事件，记录登录日志
grails.plugin.springsecurity.useSecurityEventListener = true

grails.plugin.springsecurity.rejectIfNoRule = false
grails.plugin.springsecurity.fii.rejectPublicInvocations = false

grails.plugin.springsecurity.rest.token.storage.jwt.with {
    useSignedJwt = System.getenv('USE_SIGNED_JWT') ?: true
    secret = System.getenv('JWT_SECRET') ?: 'qrD6h8K6S9503Q06Y6Rfk21TErImPYqa'
    expiration = System.getenv('JWT_EXPIRATION') ?: 3600
    useEncryptedJwt = System.getenv('USE_ENCRYPTED_JWT') ?: false
    privateKeyPath = System.getenv('PRIVATE_KEY_PATH') ?: ''
    publicKeyPath = System.getenv('PUBLIC_KEY_PATH') ?: ''
}

grails.plugin.springsecurity.securityConfigType = "InterceptUrlMap"

grails.plugin.springsecurity.interceptUrlMap = [
        [pattern: '/', access: ['permitAll']]
        , [pattern: '/api/getUploadAuthority', access: ['isFullyAuthenticated()'], httpMethod: 'GET']
        , [pattern: '/api/**', access: ['denyAll']]
]

grails.plugin.springsecurity.filterChain.chainMap = [
        [pattern: '/api/**', filters: 'JOINED_FILTERS,-exceptionTranslationFilter,-authenticationProcessingFilter,-securityContextPersistenceFilter,-rememberMeAuthenticationFilter']
]

grails.plugin.springsecurity.failureHandler.exceptionMappings = [
        [exception: org.springframework.security.authentication.CredentialsExpiredException.name, url: '/api/passwordExpired']
]

grails.plugin.springsecurity.rest.token.validation.enableAnonymousAccess = true
