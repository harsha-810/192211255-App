package com.simats.petclinicapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.simats.petclinicapp.navigation.*
import com.simats.petclinicapp.ui.LoginScreen
import com.simats.petclinicapp.ui.SplashScreen
import com.simats.petclinicapp.ui.theme.PetClinicAPPTheme
import android.widget.Toast

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PetClinicAPPTheme {
                val navController = rememberNavController()
                
                NavHost(
                    navController = navController,
                    startDestination = Splash,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable<Splash> {
                        SplashScreen(
                            onNavigateToLogin = {
                                navController.navigate(Login) {
                                    popUpTo(Splash) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable<Login> {
                        LoginScreen(
                            onLoginSuccess = { role ->
                                android.util.Log.d("AuthFlow", "Login success! Role: $role")
                                Toast.makeText(this@MainActivity, "Logged in as $role", Toast.LENGTH_SHORT).show()
                                if (role == "Doctor") {
                                    navController.navigate(DoctorDashboard) {
                                        popUpTo(Login) { inclusive = true }
                                    }
                                } else if (role == "Patient") {
                                    navController.navigate(Subscription) {
                                        popUpTo(Login) { inclusive = true }
                                    }
                                } else if (role == "Admin") {
                                    navController.navigate(AdminDashboard) {
                                        popUpTo(Login) { inclusive = true }
                                    }
                                } else {
                                    Toast.makeText(this@MainActivity, "Unknown role: $role", Toast.LENGTH_LONG).show()
                                }
                            },
                            onRegisterClick = {
                                navController.navigate(RegisterPatient)
                            },
                            onForgotPasswordClick = {
                                navController.navigate(ForgotPassword)
                            }
                        )
                    }

                    composable<RegisterPatient> {
                        com.simats.petclinicapp.ui.RegisterPatientScreen(
                            onBackToLogin = { navController.popBackStack() },
                            onRegisterSuccess = {
                                Toast.makeText(this@MainActivity, "Registration Successful. Please Login.", Toast.LENGTH_LONG).show()
                                navController.popBackStack()
                            }
                        )
                    }

                    composable<ForgotPassword> {
                        com.simats.petclinicapp.ui.ForgotPasswordScreen(
                            onBackClick = { navController.popBackStack() },
                            onOtpSent = { email ->
                                navController.navigate(ResetPassword(email = email))
                            }
                        )
                    }

                    composable<ResetPassword> { backStackEntry ->
                        val route = backStackEntry.toRoute<ResetPassword>()
                        com.simats.petclinicapp.ui.ResetPasswordScreen(
                            email = route.email,
                            onBackClick = { navController.popBackStack() },
                            onResetSuccess = {
                                Toast.makeText(this@MainActivity, "Password reset successful. Please login.", Toast.LENGTH_LONG).show()
                                navController.navigate(Login) {
                                    popUpTo(ForgotPassword) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable<DoctorDashboard> {
                        com.simats.petclinicapp.ui.DoctorDashboardScreen(
                            onLogout = {
                                com.simats.petclinicapp.network.RetrofitClient.token = ""
                                navController.navigate(Login) {
                                    popUpTo(DoctorDashboard) { inclusive = true }
                                }
                            },
                            onPrescribeClick = { appointmentId ->
                                navController.navigate(WritePrescription(appointmentId = appointmentId))
                            },
                            onPetHistoryClick = { petId ->
                                navController.navigate(PetHistory(petId = petId, isDoctor = true))
                            },
                            onStatsClick = {
                                navController.navigate(DoctorStatistics)
                            },
                            onProfileClick = {
                                navController.navigate(DoctorProfile)
                            },
                            onChangePassword = {
                                navController.navigate(ChangePassword)
                            }
                        )
                    }

                    composable<DoctorStatistics> {
                        com.simats.petclinicapp.ui.DoctorStatisticsScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable<DoctorProfile> {
                        com.simats.petclinicapp.ui.DoctorProfileScreen(
                            onBack = { navController.popBackStack() },
                            onSettingsClick = { navController.navigate(Settings(isDoctor = true)) }
                        )
                    }


                    composable<WritePrescription> { backStackEntry ->
                        val writeRx = backStackEntry.toRoute<WritePrescription>()
                        com.simats.petclinicapp.ui.AddPrescriptionScreen(
                            appointmentId = writeRx.appointmentId,
                            onCancel = { navController.popBackStack() },
                            onPrescriptionSaved = { navController.popBackStack() }
                        )
                    }

                    composable<PatientMain> {
                        com.simats.petclinicapp.ui.PatientMainScreen(
                            rootNavController = navController,
                            onLogout = {
                                com.simats.petclinicapp.network.RetrofitClient.token = ""
                                navController.navigate(Login) {
                                    popUpTo(PatientMain) { inclusive = true }
                                }
                            },
                            onSettingsClick = {
                                navController.navigate(Settings(isDoctor = false))
                            }
                        )
                    }

                    composable<AdminDashboard> {
                        com.simats.petclinicapp.ui.AdminDashboardScreen(
                            onLogout = {
                                com.simats.petclinicapp.network.RetrofitClient.token = ""
                                navController.navigate(Login) {
                                    popUpTo(AdminDashboard) { inclusive = true }
                                }
                            },
                            onSettingsClick = {
                                navController.navigate(AdminSettings)
                            }
                        )
                    }

                    composable<AdminSettings> {
                        com.simats.petclinicapp.ui.AdminSettingsScreen(
                            onBack = { navController.popBackStack() },
                            onLogout = {
                                com.simats.petclinicapp.network.RetrofitClient.token = ""
                                navController.navigate(Login) {
                                    popUpTo(AdminSettings) { inclusive = true }
                                    popUpTo(AdminDashboard) { inclusive = true }
                                }
                            },
                            onChangePassword = { navController.navigate(ChangePassword) },
                            onPrivacyPolicy = { navController.navigate(PrivacyPolicy) },
                            onTermsOfService = { navController.navigate(TermsOfService) }
                        )
                    }



                    composable<AddPet> {
                        com.simats.petclinicapp.ui.AddPetScreen(
                            onCancel = { navController.popBackStack() },
                            onPetAdded = { navController.popBackStack() }
                        )
                    }

                    composable<BookAppointment> { backStackEntry ->
                        val bookAppt = backStackEntry.toRoute<BookAppointment>()
                        com.simats.petclinicapp.ui.BookAppointmentScreen(
                            petId = bookAppt.petId,
                            initialSymptoms = bookAppt.symptoms ?: "",
                            initialDuration = bookAppt.duration ?: "",
                            priorityLevel = bookAppt.priority,
                            onCancel = { navController.popBackStack() },
                            onAppointmentBooked = { navController.popBackStack() }
                        )
                    }

                    composable<PetHistory> { backStackEntry ->
                        val history = backStackEntry.toRoute<PetHistory>()
                        com.simats.petclinicapp.ui.PetHistoryScreen(
                            petId = history.petId,
                            isDoctor = history.isDoctor,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable<Settings> { backStackEntry ->
                        val settingsRoute = backStackEntry.toRoute<Settings>()
                        com.simats.petclinicapp.ui.SettingsScreen(
                            isDoctor = settingsRoute.isDoctor,
                            onBack = { navController.popBackStack() },
                            onLogout = {
                                com.simats.petclinicapp.network.RetrofitClient.token = ""
                                navController.navigate(Login) {
                                    popUpTo(Settings(isDoctor = settingsRoute.isDoctor)) { inclusive = true }
                                    popUpTo(DoctorDashboard) { inclusive = true }
                                    popUpTo(PatientMain) { inclusive = true }
                                }
                            },
                            onChangePassword = { navController.navigate(ChangePassword) },
                            onEditProfile = { isDoc -> 
                                navController.navigate(EditProfile(isDoctor = isDoc)) 
                                },
                            onPrivacyPolicy = { navController.navigate(PrivacyPolicy) },
                            onTermsOfService = { navController.navigate(TermsOfService) }
                        )
                    }

                    composable<EditProfile> { backStackEntry ->
                        val editProfileRoute = backStackEntry.toRoute<EditProfile>()
                        com.simats.petclinicapp.ui.EditProfileScreen(
                            isDoctor = editProfileRoute.isDoctor,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable<PrivacyPolicy> {
                        com.simats.petclinicapp.ui.PolicyScreen(
                            title = "Privacy Policy",
                            content = com.simats.petclinicapp.ui.PolicyContent.PrivacyPolicy,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable<TermsOfService> {
                        com.simats.petclinicapp.ui.PolicyScreen(
                            title = "Terms of Service",
                            content = com.simats.petclinicapp.ui.PolicyContent.TermsOfService,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable<ChangePassword> {
                        com.simats.petclinicapp.ui.ChangePasswordScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable<AiAnalysis> {
                        com.simats.petclinicapp.ui.AiAnalysisScreen(
                            onBack = { navController.popBackStack() },
                            onBookAppointment = { petId, symptoms, duration, priority ->
                                navController.navigate(
                                    BookAppointment(
                                        petId = petId,
                                        symptoms = symptoms,
                                        duration = duration,
                                        priority = priority
                                    )
                                )
                            }
                        )
                    }

                    composable<FilteredAppointments> { backStackEntry ->
                        val route = backStackEntry.toRoute<FilteredAppointments>()
                        com.simats.petclinicapp.ui.PatientAppointmentsScreen(
                            onBack = { navController.popBackStack() },
                            petIdFilter = route.petId,
                            onlyUpcoming = route.onlyUpcoming
                        )
                    }

                    composable<Subscription> {
                        com.simats.petclinicapp.ui.SubscriptionScreen(
                            onPayClick = {
                                navController.navigate(PatientMain) {
                                    popUpTo(Subscription) { inclusive = true }
                                }
                            },
                            onRemindLaterClick = {
                                navController.navigate(PatientMain) {
                                    popUpTo(Subscription) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
