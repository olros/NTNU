package com.olafros.exercise5.Controller

import com.olafros.exercise5.Service.CalculatorService
import com.olafros.exercise5.dto.CalculateDTO
import com.olafros.exercise5.dto.ReturnDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
@RequestMapping("/api/calculator/")
class CalculatorController(
        val calculatorService: CalculatorService,
) {
    @PostMapping
    fun createNewAuthor(@RequestBody calculate: CalculateDTO): ResponseEntity<ReturnDTO> =
            ResponseEntity.ok(ReturnDTO(calculatorService.calculate(calculate.numbers)))
}