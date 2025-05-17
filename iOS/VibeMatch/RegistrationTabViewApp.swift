//
//  RegistrationTabViewApp.swift
//  VibeMatch
//
//  Created by Аайа Иванова on 12.05.2025.
//


import SwiftUI

struct RegistrationView: View {
    @State private var username: String = ""
    @State private var email: String = ""
    @State private var password: String = ""
    @State private var isRegistered: Bool = false
    @State private var isLogin: Bool = false

    var body: some View {
        NavigationStack {
            VStack {
                Text("Registration")
                    .font(.largeTitle)
                    .padding()

                TextField("Username", text: $username)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .accessibilityIdentifier("RegistrationUsername")
                    .padding()

                TextField("Email", text: $email)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .padding()
                    .keyboardType(.emailAddress)
                    .autocapitalization(.none)

                SecureField("Password", text: $password)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .padding()

                Button(action: {
                    registerUser()
                }) {
                    Text("Register")
                        .padding()
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(8)
                }
                .padding()

                Button(action: {
                    isLogin = true
                }) {
                    Text("Already have an account? Login")
                        .foregroundColor(.blue)
                }
                .padding()
                .navigationDestination(isPresented: $isRegistered) {
                    LoginView()
                }
            }
            .padding()
        }
    }

    private func registerUser() {
        UserDefaults.standard.set(username, forKey: "userName")
        UserDefaults.standard.set(email, forKey: "userEmail")
        UserDefaults.standard.set(password, forKey: "userPassword")
        
        isRegistered = true
    }
}
