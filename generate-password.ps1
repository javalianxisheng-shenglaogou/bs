# Generate BCrypt password hash using Java
Write-Host "=== Generating BCrypt password hash ===" -ForegroundColor Green

# Create a temporary Java file to generate BCrypt hash
$javaCode = @"
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        String hash = encoder.encode(password);
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hash);
        
        // Test the hash
        boolean matches = encoder.matches(password, hash);
        System.out.println("Hash matches: " + matches);
    }
}
"@

# Write Java file
$javaCode | Out-File -FilePath "PasswordGenerator.java" -Encoding UTF8

# Compile and run (assuming Spring Security is in classpath)
Write-Host "Compiling Java file..." -ForegroundColor Yellow

try {
    # Use the existing JAR's classpath
    $classpath = "target/cms-backend-1.0.0.jar"
    
    javac -cp $classpath PasswordGenerator.java
    java -cp "${classpath};." PasswordGenerator
    
    Write-Host "`nJava execution completed" -ForegroundColor Green
    
} catch {
    Write-Host "Java execution failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Let's try a different approach..." -ForegroundColor Yellow
    
    # Alternative: Use online BCrypt generator result
    $knownHash = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'
    Write-Host "Using known BCrypt hash for 'password': $knownHash"
    
    # Update database with known hash
    $updateQuery = "UPDATE users SET password_hash = '$knownHash' WHERE username IN ('admin', 'superadmin');"
    & "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -h localhost -P 3306 -u root -p123456 multi_site_cms -e $updateQuery
    Write-Host "Database updated with known hash for password 'password'" -ForegroundColor Green
}

# Clean up
if (Test-Path "PasswordGenerator.java") {
    Remove-Item "PasswordGenerator.java"
}
if (Test-Path "PasswordGenerator.class") {
    Remove-Item "PasswordGenerator.class"
}
