package com.safedjio.internship.task4_minispring;

import com.safedjio.internship.task4_minispring.beans.UserController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MiniApplicationContextTest {
    String basePackage = "com.safedjio.internship.task4_minispring.beans";

    @Test
    void context_shouldScanCreateAndInjectBeansCorrectly() {
        MiniApplicationContext context = new MiniApplicationContext(basePackage);

        UserController controller = context.getBean(UserController.class);

        assertNotNull(controller, "Контроллер не должен быть null");
        assertNotNull(controller.getUserService(), "UserService внутри контроллера не должен быть null! DI не сработал.");
        assertEquals("Controller Initialized with user: UserName", controller.getInitMessage());
    }

    @Test
    void getBean_withUnknownClass_shouldThrowException() {
        MiniApplicationContext context = new MiniApplicationContext(basePackage);
        assertThrows(RuntimeException.class, () -> context.getBean(String.class));
    }
}