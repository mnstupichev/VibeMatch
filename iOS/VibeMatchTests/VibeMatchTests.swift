//
//  VibeMatchTests.swift
//  VibeMatchTests
//
//  Created by Аайа Иванова on 12.05.2025.
//

import XCTest

class VibeMatchUITests: XCTestCase {
    
    var app: XCUIApplication!
    
    override func setUp() {
        super.setUp()
        app = XCUIApplication()
        app.launchArguments.append("--uitesting")
        app.launch()
    }
    
    func testRegistrationFlow() {
        // Registration
        app.textFields["RegistrationUsername"].tap()
        app.typeText("TestUser")
        
        app.textFields["Email"].tap()
        app.typeText("test@example.com")
        
        app.secureTextFields["Password"].tap()
        app.typeText("password123")
        
        app.buttons["Register"].tap()
        
        // Verify navigation to Login
        XCTAssertTrue(app.staticTexts["Login"].exists)
    }
    
    func testLoginAndNavigation() {
        // Setup test user
        app.launchEnvironment = ["TEST_USER_EMAIL": "test@example.com", "TEST_USER_PASSWORD": "password123"]
        
        // Navigate to Login
        app.buttons["Already have an account? Login"].tap()
        
        // Login
        app.textFields["LoginEmail"].tap()
        app.typeText("test@example.com")
        
        app.secureTextFields["Password"].tap()
        app.typeText("password123")
        
        app.buttons["Login"].tap()
        
        // Verify TabView navigation
        XCTAssertTrue(app.buttons["Form"].exists)
        XCTAssertTrue(app.buttons["Create"].exists)
        XCTAssertTrue(app.buttons["Profile"].exists)
    }
    
    func testFormCreation() {
        // Navigate to Create tab
        app.buttons["Create"].tap()
        
        // Fill form
        app.textFields["FormName"].tap()
        app.typeText("Meeting 1")
        
        app.textFields["City"].tap()
        app.typeText("London")
        
        app.textFields["Gender"].tap()
        app.typeText("Male")
        
        app.textFields["Age"].tap()
        app.typeText("30")
        
        app.buttons["Save Form"].tap()
        
        // Verify success message
        XCTAssertTrue(app.staticTexts["Form saved!"].exists)
    }
}
