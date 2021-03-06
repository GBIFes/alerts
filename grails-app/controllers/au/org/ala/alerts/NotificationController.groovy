package au.org.ala.alerts

import au.org.ala.web.AlaSecured
import grails.converters.JSON
import org.apache.http.HttpStatus

class NotificationController {

    def notificationService
    def emailService
    def userService
    def authService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def myalerts = { redirect(action: "myAlerts", params: params) }

    def myAlerts = {
        User user = userService.getUser()
        log.debug('Viewing my alerts :  ' + user)
        userService.getUserAlertsConfig(user)
    }

    def addMyAlert = {
        def user
        if (authService.userInRole("ROLE_ADMIN")) {
            user = userService.getUserById(params.userId)
        } else {
            user = userService.getUser()
        }

        if (!user) {
            response.status = HttpStatus.SC_NOT_FOUND
            response.sendError(HttpStatus.SC_NOT_FOUND, "Unrecognised user")
        } else {
            log.debug('add my alert ' + params.id)
            def notificationInstance = new Notification()
            notificationInstance.query = Query.findById(params.id)
            notificationInstance.user = user
            //does this already exist?
            def exists = Notification.findByQueryAndUser(notificationInstance.query, notificationInstance.user)
            if (!exists) {
                log.info("Adding alert for user: " + notificationInstance.user + ", query id: " + params.id)
                notificationInstance.save(flush: true)
            } else {
                log.info("NOT Adding alert for user: " + notificationInstance.user + ", query id: " + params.id + ", already exists...")
            }
            return null
        }
    }

    def deleteMyAlert = {
        def user
        if (authService.userInRole("ROLE_ADMIN")) {
            user = userService.getUserById(params.userId)
        } else {
            user = userService.getUser()
        }

        if (!user) {
            response.status = HttpStatus.SC_NOT_FOUND
            response.sendError(HttpStatus.SC_NOT_FOUND, "Unrecognised user")
        } else {
            def query = Query.get(params.id)
            log.debug('Deleting my alert :  ' + params.id + ' for user : ' + authService.getDisplayName())

            def notificationInstance = Notification.findByUserAndQuery(user, query)
            if (notificationInstance) {
                log.debug('Deleting my notification :  ' + params.id)
                notificationInstance.each { it.delete(flush: true) }
            } else {
                log.error('*** Unable to find  my notification - no delete :  ' + params.id)
            }
            return null
        }
    }

    def deleteMyAlertWR = {
        def user
        if (authService.userInRole("ROLE_ADMIN")) {
            user = userService.getUserById(params.userId)
        } else {
            user = userService.getUser()
        }

        //this is a hack to get around a CAS issue
        if (user == null) {
            user = User.findByEmail(params.userId)
        }

        def query = Query.get(params.id)
        log.debug('Deleting my alert :  ' + params.id + ' for user : ' + user)

        def notificationInstance = Notification.findByUserAndQuery(user, query)
        if (notificationInstance) {
            log.debug('Deleting my notification :  ' + params.id)
            notificationInstance.each { it.delete(flush: true) }
        } else {
            log.error('*** Unable to find  my notification - no delete :  ' + params.id)
        }
        redirect(action: 'myAlerts')
    }

    def changeFrequency = {
        def user = userService.getUser()
        log.debug("Changing frequency to: " + params.frequency + " for user ${user}")
        user.frequency = Frequency.findByName(params.frequency)
        user.save(flush: true)
        return null
    }

    def checkNow = {
        Notification notification = Notification.get(params.id)
        boolean sendUpdateEmail = notificationService.checkStatus(notification.query)
        if (sendUpdateEmail) {
            emailService.sendNotificationEmail(notification)
        }
        redirect(action: "show", params: params)
    }

    def index = {
        //if is ADMIN, then index page
        //else redirect to /notification/myAlerts
        if (authService.userInRole("ADMIN")) {
            redirect(action: "admin")
        } else {
            redirect(action: "myAlerts")
        }
    }

    def admin = {

    }
}
