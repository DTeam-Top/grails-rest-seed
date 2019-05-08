package top.dteam.earth.backend.user

import grails.gorm.services.Service

@Service(LoginHistory)
interface LoginHistoryService {

    LoginHistory get(Serializable id)

    List<LoginHistory> list(Map args)

    Long count()

    void delete(Serializable id)

    LoginHistory save(LoginHistory loginHistory)

}
