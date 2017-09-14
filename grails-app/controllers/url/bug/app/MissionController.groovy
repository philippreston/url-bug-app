package url.bug.app

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class MissionController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Mission.list(params), model:[missionCount: Mission.count()]
    }

    def show(Mission mission) {
        respond mission
    }

    def create() {
        respond new Mission(params)
    }

    @Transactional
    def save(Mission mission) {
        if (mission == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (mission.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond mission.errors, view:'create'
            return
        }

        mission.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'mission.label', default: 'Mission'), mission.id])
                redirect mission
            }
            '*' { respond mission, [status: CREATED] }
        }
    }

    def edit(Mission mission) {
        respond mission
    }

    @Transactional
    def update(Mission mission) {
        if (mission == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (mission.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond mission.errors, view:'edit'
            return
        }

        mission.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'mission.label', default: 'Mission'), mission.id])
                redirect mission
            }
            '*'{ respond mission, [status: OK] }
        }
    }

    @Transactional
    def delete(Mission mission) {

        if (mission == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        mission.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'mission.label', default: 'Mission'), mission.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'mission.label', default: 'Mission'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
