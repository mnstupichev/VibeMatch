//
//  TabView.swift
//  VibeMatch
//
//  Created by Аайа Иванова on 12.05.2025.
//


import SwiftUI

struct TabViewScreen: View {
    var body: some View {
        TabView {
            FormView()
                .tabItem {
                    Label("Form", systemImage: "person.3.sequence.fill")
                }
            
            CreateView()
                .tabItem {
                    Label("Create", systemImage: "plus")
                }

            ProfileView()
                .tabItem {
                    Label("Profile", systemImage: "person.crop.circle")
                }
        }
    }
}


struct FormView: View {
    @State private var meetings: [[String: String]] = []

    var body: some View {
        NavigationView {
            List(meetings, id: \.self) { meeting in
                VStack(alignment: .leading) {
                    Text(meeting["title"] ?? "Unknown")
                        .font(.headline)
                    Text("City: \(meeting["city"] ?? "Not specified")")
                    Text("Gender: \(meeting["gender"] ?? "Not specifield")")
                    Text("Age: \(meeting["age"] ?? "Not specifield")")
                }
                .padding()
            }
            .onAppear(perform: loadMeetings)
        }
    }

    private func loadMeetings() {
        meetings = UserDefaults.standard.array(forKey: "forms") as? [[String: String]] ?? []
    }
}


struct CreateView: View {
    @State private var formName: String = ""
    @State private var formBio: String = ""
    @State private var formCity: String = ""
    @State private var formGender: String = ""
    @State private var formAge: String = ""
    @State private var saveSuccess: Bool = false

    var body: some View {
        VStack {
            Text("Create Form")
                .font(.largeTitle)
                .padding()

            TextField("Name", text: $formName)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .accessibilityIdentifier("FormName")
                .padding()

            TextField("City", text: $formCity)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .padding()

            TextField("Gender", text: $formGender)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .padding()

            TextField("Age", text: $formAge)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .padding()

            Button(action: {
                saveForm()
            }) {
                Text("Save Form")
                    .padding()
                    .background(Color.blue)
                    .foregroundColor(.white)
                    .cornerRadius(8)
            }
            .padding()
            
            if saveSuccess {
                Text("Form saved!")
                    .foregroundColor(.green)
            }
        }
        .padding()
    }

    private func saveForm() {
        var meetings = UserDefaults.standard.array(forKey: "forms") as? [[String: String]] ?? []

        let newMeeting: [String: String] = [
            "Name": formName,
            "City": formCity,
            "Gender": formGender,
            "Age": formAge
        ]
    
        meetings.append(newMeeting)
        
        UserDefaults.standard.set(meetings, forKey: "forms")

        formName = ""
        formCity = ""
        formGender = ""
        formAge = ""
       
        saveSuccess = true
    }
}


struct ProfileView: View {
    @State private var userName: String = ""
    @State private var userEmail: String = ""
    @State private var userForms: [[String: String]] = []

    var body: some View {
        VStack {
            Text("Profile")
                .font(.largeTitle)
                .padding()

            VStack(alignment: .leading) {
                Text("Username: \(userName)")
                Text("Email: \(userEmail)")
            }
            .padding()
            
            Divider()
            
            Text("My Form")
                .font(.title2)
                .padding(.top)
            
            if userForms.isEmpty {
                Text("There is no Form")
                    .foregroundColor(.gray)
                    .padding()
            } else {
                List(userForms, id: \.self) { form in
                    VStack(alignment: .leading) {
                        Text(form["Name"] ?? "Без названия")
                            .font(.headline)
                        Text("City: \(form["City"] ?? "не указан")")
                        Text("Gender: \(form["Gender"] ?? "не указан")")
                        Text("Age: \(form["Age"] ?? "не указана")")
                    }
                    .padding(.vertical)
                }
                .listStyle(PlainListStyle())
            }
            
            Spacer()
        }
        .onAppear {
            loadProfile()
            loadForms()
        }
    }

    private func loadProfile() {
        userName = UserDefaults.standard.string(forKey: "userName") ?? "Неизвестно"
        userEmail = UserDefaults.standard.string(forKey: "userEmail") ?? "Неизвестно"
    }
    
    private func loadForms() {
        userForms = UserDefaults.standard.array(forKey: "forms") as? [[String: String]] ?? []
    }
}

