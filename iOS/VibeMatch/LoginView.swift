//
//  LoginView.swift
//  VibeMatch
//
//  Created by Аайа Иванова on 12.05.2025.
//


import SwiftUI

struct LoginView: View {
    @Environment(\.dismiss) var dismiss
    @State private var loginEmail: String = ""
    @State private var loginPassword: String = ""
    @State private var loginError: String = ""
    @State private var isLoggedIn: Bool = false
    
    var body: some View {
        NavigationStack {
            VStack {
                Text("Login")
                    .font(.largeTitle)
                    .padding()
                
                TextField("Email", text: $loginEmail)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .accessibilityIdentifier("LoginEmail")
                    .padding()
                    .keyboardType(.emailAddress)
                    .autocapitalization(.none)
                
                SecureField("Password", text: $loginPassword)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .padding()
                
                Button(action: {
                    loginUser()
                }) {
                    Text("Login")
                        .padding()
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(8)
                }
                .padding()
                
                if !loginError.isEmpty {
                    Text(loginError)
                        .foregroundColor(.red)
                        .padding()
                }
                
            }
            .padding()
        }
        .navigationDestination(isPresented: $isLoggedIn) {
            TabViewScreen()
        }
    }
    
    private func loginUser() {
        let savedEmail = UserDefaults.standard.string(forKey: "userEmail") ?? ""
        let savedPassword = UserDefaults.standard.string(forKey: "userPassword") ?? ""
        
        if loginEmail == savedEmail && loginPassword == savedPassword {
            isLoggedIn = true
        } else {
            loginError = "Incorrect email or password"
        }
    }
}
