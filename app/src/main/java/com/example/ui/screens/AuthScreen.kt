package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.MainViewModel
import com.example.ui.theme.DarkBackground
import com.example.util.GoogleSignInManager
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    viewModel: MainViewModel,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (maxWidth > 800.dp) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Left Pane
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(DarkBackground)
                ) {
                    AsyncImage(
                        model = "https://lh3.googleusercontent.com/aida-public/AB6AXuDdRi1zgITil5SnoPu9mFiiQsfJlv0yQG_J9mH1cuJqnpIBnGHZnvkKIywPCf0XCG3SK7FjN6Emo2ncbzrbtshXWPFg-fJzVkMiEVciIRPL5Zh3Oo1hqQqdbM7KLy19A8znzRUuAclfg-DVj6sxQri4lvallk-2BOWZIj8KR_yC5oM6kOY_1SuAOq2inKWu3-tpNlbjcFWW6MZGBMSNayYYpYyQdgz0y6dEMkL82f_En84SbhMVkFT-",
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        alpha = 0.4f
                    )
                    
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(64.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(imageVector = Icons.Default.Gavel, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                            Text("Contador Jurídico Pro", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(48.dp))
                        Text(
                            text = "Streamline Your Legal Audits",
                            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Professional tools designed to bring clarity, structure, and control to your legal processes. Secure access to your tailored auditing environment.",
                            style = MaterialTheme.typography.titleLarge.copy(lineHeight = 32.sp),
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
                
                // Right Pane
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    AuthForm(viewModel, onAuthSuccess, isMobile = false)
                }
            }
        } else {
            // Mobile Single Pane
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                AuthForm(viewModel, onAuthSuccess, isMobile = true)
            }
        }
    }
}

@Composable
fun AuthForm(
    viewModel: MainViewModel,
    onAuthSuccess: () -> Unit,
    isMobile: Boolean
) {
    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("dr.roberto@contadorjuridico.com.br") }
    var password by remember { mutableStateOf("12345678") }
    var name by remember { mutableStateOf("Dr. Roberto Silva") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 480.dp)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isMobile) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(bottom = 32.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Gavel, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                        Text("Contador Jurídico Pro", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                    }
                }
                
                Text(
                    text = if (isSignUp) "Create Account" else "Welcome Back",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isSignUp) "Please fill in the details to join us." else "Please enter your details to sign in.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                if (isSignUp) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Name", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = { Text("John Doe") },
                            leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                            singleLine = true,
                            shape = MaterialTheme.shapes.large,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedBorderColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Email", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("name@example.com") },
                        leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                        singleLine = true,
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.fillMaxWidth().testTag("auth_email_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Password", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("••••••••") },
                        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.fillMaxWidth().testTag("auth_password_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (!isSignUp) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                            )
                            Text("Remember me", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        TextButton(onClick = { }) {
                            Text("Forgot password?", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                } else {
                    Spacer(modifier = Modifier.height(32.dp))
                }
                
                Button(
                    onClick = {
                        viewModel.setLoggedIn(true, name, email)
                        onAuthSuccess()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("auth_submit_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = CircleShape
                ) {
                    Text(
                        text = if (isSignUp) "Sign Up" else "Sign In",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Text(
                        text = "Or continue with",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                val manager = GoogleSignInManager(context)
                                val (success, loggedName) = manager.signInWithGoogle()
                                if (success) {
                                    viewModel.setLoggedIn(true, loggedName, email)
                                    onAuthSuccess()
                                } else {
                                    viewModel.setLoggedIn(true, "Dr. Roberto Silva (Dev Auth)", email)
                                    onAuthSuccess()
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("google_login_button"),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Text("G", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF4285F4))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Google", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    
                    OutlinedButton(
                        onClick = {
                            viewModel.setLoggedIn(true, "Dr. Roberto Silva (Apple)", email)
                            onAuthSuccess()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("apple_login_button"),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneIphone,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Apple", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isSignUp) "Already have an account?" else "Don't have an account?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(onClick = { isSignUp = !isSignUp }) {
                        Text(
                            text = if (isSignUp) "Sign In" else "Create Account",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp), 
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Terms of Service", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("•", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Privacy Policy", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
