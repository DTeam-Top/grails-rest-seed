package top.dteam.earth.backend

import grails.validation.ValidationException

import static org.springframework.http.HttpStatus.*

class ExampleController {

    ExampleService exampleService

    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: 'POST', update: 'PUT', delete: 'DELETE']

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond exampleService.list(params)
    }

    def show(Long id) {
        respond exampleService.get(id)
    }

    def save() {
        Example example = new Example()
        bindData(example, request.JSON)

        try {
            exampleService.save(example)
        } catch (ValidationException e) {
            respond example.errors, view: 'create'
            return
        }

        respond example, [status: CREATED, view: 'show']
    }

    def update(Long id) {
        Example example = exampleService.get(id)
        if (example == null) {
            render status: NOT_FOUND
            return
        }

        try {
            bindData(example, request.JSON)
            exampleService.save(example)
        } catch (ValidationException e) {
            respond example.errors, view: 'edit'
            return
        }

        respond example, [status: OK, view: 'show']
    }

    def delete(Long id) {
        if (id == null) {
            render status: NOT_FOUND
            return
        }

        exampleService.delete(id)

        render status: NO_CONTENT
    }

}
