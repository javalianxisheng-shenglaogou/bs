# Add more test data script

# Login first to get token
$loginBody = @{
    usernameOrEmail = "superadmin"
    password = "admin123"
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
$token = $loginResponse.data.accessToken
Write-Host "Login successful, Token: $($token.Substring(0, 50))..."

# Set request headers
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}
# Add more content
$contents = @(
    @{
        title = "React Hooks Deep Dive"
        content = "React Hooks is a new feature introduced in React 16.8. This article explores useState, useEffect, useContext and other common Hooks usage and best practices."
        summary = "Learn React Hooks usage and best practices"
        contentType = "ARTICLE"
        status = "PUBLISHED"
        siteId = 1
        allowComment = $true
        isTop = $false
        seoTitle = "React Hooks Deep Dive - Modern React Development"
        seoDescription = "Learn React Hooks core concepts and master useState, useEffect usage"
        seoKeywords = "React,Hooks,useState,useEffect,frontend"
    },
    @{
        title = "Node.js Performance Optimization"
        content = "Node.js performance optimization is crucial for high-concurrency scenarios. This article covers memory management, event loop optimization, database connection pooling, and caching strategies."
        summary = "Comprehensive Node.js performance optimization strategies"
        contentType = "ARTICLE"
        status = "PUBLISHED"
        siteId = 1
        allowComment = $true
        isTop = $false
        seoTitle = "Node.js Performance Optimization Guide"
        seoDescription = "Master Node.js performance optimization techniques"
        seoKeywords = "Node.js,performance,optimization,memory,caching"
    },
    @{
        title = "Docker Containerization Guide"
        content = "Docker containerization has become the standard for modern application deployment. This article covers Dockerfile writing, image building, container orchestration, and production best practices."
        summary = "Complete Docker containerization deployment tutorial"
        contentType = "ARTICLE"
        status = "PUBLISHED"
        siteId = 1
        allowComment = $true
        isTop = $true
        seoTitle = "Docker Containerization Practical Guide"
        seoDescription = "Learn Docker containerization technology and deployment best practices"
        seoKeywords = "Docker,containerization,deployment,DevOps,microservices"
    },
    @{
        title = "TypeScript Advanced Type System"
        content = "TypeScript's type system is one of its most powerful features. This article explores generics, conditional types, mapped types, template literal types and other advanced features."
        summary = "Master TypeScript advanced type features"
        contentType = "ARTICLE"
        status = "PUBLISHED"
        siteId = 1
        allowComment = $true
        isTop = $false
        seoTitle = "TypeScript Advanced Type System Guide"
        seoDescription = "Learn TypeScript advanced type features including generics and conditional types"
        seoKeywords = "TypeScript,type system,generics,frontend"
    },
    @{
        title = "About Us"
        content = "We are a team focused on technology sharing and knowledge dissemination. We are committed to providing developers with high-quality technical articles, tutorials and best practices."
        summary = "Learn about our team and mission"
        contentType = "PAGE"
        status = "PUBLISHED"
        siteId = 1
        allowComment = $false
        isTop = $false
        seoTitle = "About Us - Tech Sharing Team"
        seoDescription = "Learn about our team background, mission and values"
        seoKeywords = "about us,team,tech sharing"
    },
    @{
        title = "Contact Us"
        content = "If you have any questions, suggestions or cooperation intentions, please contact us through the following methods: Email: contact@example.com Phone: +86 138-0000-0000"
        summary = "Contact information and address"
        contentType = "PAGE"
        status = "PUBLISHED"
        siteId = 1
        allowComment = $false
        isTop = $false
        seoTitle = "Contact Us - Get Technical Support"
        seoDescription = "Contact us through various methods for technical support"
        seoKeywords = "contact us,technical support,cooperation"
    }
)
Write-Host "Starting to add $($contents.Count) contents..."

$successCount = 0
foreach ($content in $contents) {
    try {
        $contentJson = $content | ConvertTo-Json -Depth 3
        $response = Invoke-RestMethod -Uri "http://localhost:8080/api/contents" -Method POST -Headers $headers -Body $contentJson
        Write-Host "✓ Successfully added content: $($content.title)"
        $successCount++
    }
    catch {
        Write-Host "✗ Failed to add content: $($content.title) - $($_.Exception.Message)"
    }
}

Write-Host "`nSummary:"
Write-Host "Successfully added: $successCount contents"
Write-Host "Failed: $($contents.Count - $successCount) contents"

# Get latest content statistics
Write-Host "`nGetting latest content statistics..."
$statsResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/contents/stats?siteId=1" -Method GET -Headers $headers
Write-Host "Content Statistics:"
Write-Host "- Total: $($statsResponse.data.totalCount)"
Write-Host "- Published: $($statsResponse.data.publishedCount)"
Write-Host "- Draft: $($statsResponse.data.draftCount)"
Write-Host "- Articles: $($statsResponse.data.articleCount)"
Write-Host "- Pages: $($statsResponse.data.pageCount)"
