package ru.practicum.onlineStore.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFound(NoHandlerFoundException ex, Model model) {
        log.error("Страница не найдена: ", ex);
        model.addAttribute("errorTitle", "Страница не найдена");
        model.addAttribute("errorMessage", "Упс! Такой страницы не существует.");
        return "error/error";
    }

    @ExceptionHandler(SQLException.class)
    public String handleDatabaseError(SQLException ex, Model model) {
        log.error("Ошибка базы данных: ", ex);
        model.addAttribute("errorTitle", "Ошибка базы данных");
        model.addAttribute("errorMessage", "Произошла проблема при обращении к базе. Попробуйте позже.");
        return "error/error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralError(Exception ex, Model model) {
        log.error("Внутренняя ошибка: ", ex);
        model.addAttribute("errorTitle", "Внутренняя ошибка");
        model.addAttribute("errorMessage", "Что-то пошло не так. Мы уже работаем над этим!");
        return "error/error";
    }

}
