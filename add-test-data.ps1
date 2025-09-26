# Add test data via API

Write-Host "Logging in to get token..."

# Login to get token
$loginBody = @{
    usernameOrEmail = "superadmin"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
    $token = $loginResponse.data.accessToken
    Write-Host "Login successful, Token: $($token.Substring(0, 20))..."

    $headers = @{
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json"
    }

    # Add categories
    Write-Host "`nAdding categories..."

    $categories = @(
        @{ name = "Tech Articles"; code = "tech"; description = "Technology related articles"; siteId = 1; sortOrder = 1 },
        @{ name = "Products"; code = "products"; description = "Product introduction content"; siteId = 1; sortOrder = 2 },
        @{ name = "News"; code = "news"; description = "Company news and industry updates"; siteId = 1; sortOrder = 3 }
    )
    
    $categoryIds = @{}
    foreach ($category in $categories) {
        try {
            $categoryBody = $category | ConvertTo-Json
            $categoryResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/categories" -Method POST -Headers $headers -Body $categoryBody
            $categoryIds[$category.code] = $categoryResponse.data.id
            Write-Host "Category added successfully: $($category.name) (ID: $($categoryResponse.data.id))"
        } catch {
            Write-Host "Failed to add category: $($category.name) - $($_.Exception.Message)"
        }
    }

    # Add subcategories
    if ($categoryIds["tech"]) {
        $subCategories = @(
            @{ name = "Java Development"; code = "java"; description = "Java development articles"; siteId = 1; parentCategoryId = $categoryIds["tech"]; sortOrder = 1 },
            @{ name = "Frontend Development"; code = "frontend"; description = "Frontend development articles"; siteId = 1; parentCategoryId = $categoryIds["tech"]; sortOrder = 2 }
        )
        
        foreach ($subCategory in $subCategories) {
            try {
                $subCategoryBody = $subCategory | ConvertTo-Json
                $subCategoryResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/categories" -Method POST -Headers $headers -Body $subCategoryBody
                $categoryIds[$subCategory.code] = $subCategoryResponse.data.id
                Write-Host "Subcategory added successfully: $($subCategory.name) (ID: $($subCategoryResponse.data.id))"
            } catch {
                Write-Host "Failed to add subcategory: $($subCategory.name) - $($_.Exception.Message)"
            }
        }
    }

    # Add content
    Write-Host "`nAdding content..."
    
    $contents = @(
        @{
            title = "Spring Boot Getting Started Guide"
            slug = "spring-boot-guide"
            content = "# Spring Boot Getting Started Guide`n`nSpring Boot is a rapid development framework based on the Spring framework.`n`n## Key Features`n`n1. **Auto Configuration**: Spring Boot can automatically configure applications`n2. **Starter Dependencies**: Provides starter dependencies to simplify dependency management`n3. **Embedded Servers**: Built-in Tomcat, Jetty servers`n4. **Production Ready**: Provides health checks and monitoring features"
            excerpt = "Spring Boot is a rapid development framework based on Spring. This article introduces the main features of Spring Boot."
            type = "ARTICLE"
            status = "PUBLISHED"
            siteId = 1
            categoryId = $categoryIds["java"]
        },
        @{
            title = "Vue.js 3.0 New Features"
            slug = "vue3-new-features"
            content = "# Vue.js 3.0 New Features`n`nVue.js 3.0 brings many exciting new features and improvements.`n`n## Composition API`n`nComposition API is one of the most important new features in Vue 3.`n`n## Performance Improvements`n`n- Smaller bundle size`n- Faster rendering speed`n- Better Tree-shaking support"
            excerpt = "Vue.js 3.0 brings Composition API, performance improvements and better TypeScript support."
            type = "ARTICLE"
            status = "PUBLISHED"
            siteId = 1
            categoryId = $categoryIds["frontend"]
        },
        @{
            title = "Multi-Site CMS Architecture Design"
            slug = "multi-site-cms-architecture"
            content = "# Multi-Site CMS Architecture Design`n`nThis article introduces the architecture design of multi-site content management system.`n`n## System Architecture`n`n### Backend Architecture`n- Spring Boot 2.7.x`n- Spring Security + JWT`n- Spring Data JPA + Hibernate`n- MySQL Database"
            excerpt = "Introduction to multi-site CMS system architecture design, including technology selection and core features."
            type = "ARTICLE"
            status = "PUBLISHED"
            siteId = 1
            categoryId = $categoryIds["tech"]
        },
        @{
            title = "Product Features Introduction"
            slug = "product-features"
            content = "# Product Features Introduction`n`nOur multi-site CMS system provides rich features to meet different user needs.`n`n## Core Features`n`n### Site Management`n- Create and configure multiple independent sites`n- Custom domains and themes`n- Site status management"
            excerpt = "Detailed introduction to the core features and technical characteristics of multi-site CMS system."
            type = "PAGE"
            status = "PUBLISHED"
            siteId = 1
            categoryId = $categoryIds["products"]
        }
    )
    
    foreach ($content in $contents) {
        try {
            $contentBody = $content | ConvertTo-Json
            $contentResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/contents" -Method POST -Headers $headers -Body $contentBody
            Write-Host "Content added successfully: $($content.title) (ID: $($contentResponse.data.id))"
        } catch {
            Write-Host "Failed to add content: $($content.title) - $($_.Exception.Message)"
        }
    }

    Write-Host "`nTest data addition completed!"

} catch {
    Write-Host "Login failed: $($_.Exception.Message)"
}
