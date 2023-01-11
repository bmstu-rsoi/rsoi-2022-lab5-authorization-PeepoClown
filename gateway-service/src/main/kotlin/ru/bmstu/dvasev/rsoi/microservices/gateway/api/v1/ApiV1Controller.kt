package ru.bmstu.dvasev.rsoi.microservices.gateway.api.v1

import mu.KotlinLogging
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.bmstu.dvasev.rsoi.microservices.cars.model.GetCarsRq
import ru.bmstu.dvasev.rsoi.microservices.gateway.action.CarsGetAction
import ru.bmstu.dvasev.rsoi.microservices.gateway.action.RentCarAction
import ru.bmstu.dvasev.rsoi.microservices.gateway.action.RentEndAction
import ru.bmstu.dvasev.rsoi.microservices.gateway.action.UserRentGetAction
import ru.bmstu.dvasev.rsoi.microservices.gateway.model.CreateRentalRequest
import ru.bmstu.dvasev.rsoi.microservices.gateway.model.CreateRentalRequestWithUser
import javax.validation.Valid

@RestController
@RequestMapping(
    path = ["api/v1"]
)
class ApiV1Controller(
    private val carsGetAction: CarsGetAction,
    private val rentCarAction: RentCarAction,
    private val userRentGetAction: UserRentGetAction,
    private val rentEndAction: RentEndAction
) {

    private val log = KotlinLogging.logger {}

    @GetMapping("callback")
    fun receiveToken() {
        return
    }

    @GetMapping(
        path = ["cars"],
        produces = [APPLICATION_JSON_VALUE]
    )
    fun getAvailableCars(
        authentication: Authentication,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?,
        @RequestParam("showAll") showAll: Boolean?
    ): ResponseEntity<*> {
        val request = GetCarsRq(
            page = page?.minus(1),
            size = size,
            showAll = showAll
        )
        log.debug { "[User: ${authentication.name}] Received new get available cars request. $request" }
        val response = carsGetAction.process(request)
        log.debug { "Return get available cars response. $response" }
        return response
    }

    @PostMapping(
        path = ["rental"],
        consumes = [APPLICATION_JSON_VALUE],
        produces = [APPLICATION_JSON_VALUE]
    )
    fun createCarRent(
        authentication: Authentication,
        @Valid @RequestBody request: CreateRentalRequest
    ): ResponseEntity<*> {
        val username = authentication.name
        val requestWithUser = CreateRentalRequestWithUser(
            carUid = request.carUid!!,
            dateFrom = request.dateFrom!!,
            dateTo = request.dateTo!!,
            username = username
        )
        log.debug { "[User: ${username}] Received new create car rent request. $requestWithUser" }
        val response = rentCarAction.process(requestWithUser)
        log.debug { "Return rent car response. $response" }
        return response
    }

    @GetMapping(
        path = ["rental/{rentalUid}"],
        produces = [APPLICATION_JSON_VALUE]
    )
    fun getRentByUser(
        authentication: Authentication,
        @PathVariable("rentalUid") rentalUid: String
    ): ResponseEntity<*> {
        val username = authentication.name
        log.debug { "[User: ${username}] Received new get user $username rent request. $rentalUid" }
        val response = userRentGetAction.getUserRent(username, rentalUid)
        log.debug { "Return get user $username rent response. $response" }
        return response
    }

    @GetMapping(
        path = ["rental"],
        produces = [APPLICATION_JSON_VALUE]
    )
    fun getRentsByUser(
        authentication: Authentication
    ): ResponseEntity<*> {
        val username = authentication.name
        log.debug { "[User: ${username}] Received new get user $username rents request" }
        val response = userRentGetAction.getUserRents(username)
        log.debug { "Return get user $username rents response. $response" }
        return response
    }

    @DeleteMapping(
        path = ["rental/{rentalUid}"],
        produces = [APPLICATION_JSON_VALUE]
    )
    fun cancelRent(
        authentication: Authentication,
        @PathVariable("rentalUid") rentalUid: String
    ): ResponseEntity<*> {
        val username = authentication.name
        log.debug { "[User: ${username}] Received new cancel rent request. $rentalUid" }
        val response = rentEndAction.cancelRent(username = username, rentalUid = rentalUid)
        log.debug { "Return cancel rent response. $response" }
        return response
    }

    @PostMapping(
        path = ["rental/{rentalUid}/finish"],
        produces = [APPLICATION_JSON_VALUE]
    )
    fun finishRent(
        authentication: Authentication,
        @PathVariable("rentalUid") rentalUid: String
    ): ResponseEntity<*> {
        val username = authentication.name
        log.debug { "[User: ${username}] Received new finish rent request. $rentalUid" }
        val response = rentEndAction.finishRent(username = username, rentalUid = rentalUid)
        log.debug { "Return finish rent response. $response" }
        return response
    }
}
