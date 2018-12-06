package top.dteam.earth.backend

class UrlMappings {

    static mappings = {
        delete "/$controller/$id(.$format)?"(action: "delete")
        get "/$controller(.$format)?"(action: "index")
        get "/$controller/$id(.$format)?"(action: "show")
        post "/$controller(.$format)?"(action: "save")
        put "/$controller/$id(.$format)?"(action: "update")
        patch "/$controller/$id(.$format)?"(action: "patch")

        "/"(controller: 'application', action: 'index')
        "500"(view: '/error')
        "404"(view: '/notFound')

        "/api/register"(controller: 'User', action: 'register')
        "/api/self"(controller: 'User', action: 'self')
        "/api/updatePersonalInfo"(controller: 'User', action: 'updatePersonalInfo')
        "/api/changePassword"(controller: 'User', action: 'changePassword')
        "/api/sendSmsCode"(controller: 'User', action: 'sendSmsCode')
        "/api/users"(resources: 'Management')
        "/api/users/resetPassword"(controller: 'Management', action: 'resetPassword')
        get "/api/getUploadAuthority"(controller: 'AliyunOSS', action: 'getUploadAuthority')
    }
}
