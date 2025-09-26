# PowerShell script to batch update content, sites, and categories

# API Base URL
$apiBaseUrl = "http://localhost:8080/api"

# Admin credentials
$adminUsername = "admin"
$adminPassword = "password"

# Login to get JWT token
$loginBody = @{
    usernameOrEmail = $adminUsername
    password = $adminPassword
} | ConvertTo-Json

try {
    $loginResponse = Invoke-WebRequest -Uri "$apiBaseUrl/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
    $loginResponseContent = $loginResponse.Content | ConvertFrom-Json
    $token = $loginResponseContent.data.accessToken
    Write-Host "Successfully logged in as ${adminUsername}"
} catch {
    $errorMessage = $_.Exception.Message
    Write-Host "Failed to log in: ${errorMessage}"
    if ($_.Exception.Response) {
        $responseBody = $_.Exception.Response.Content.ReadAsStringAsync().Result
        Write-Host "Response body: $responseBody"
    }
    exit
}

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

Write-Host "Batch update script started..."

# Delete all content
try {
    $contentsResponse = Invoke-WebRequest -Uri "$apiBaseUrl/contents?page=1&size=100" -Method GET -Headers $headers
    $contentsResponseContent = $contentsResponse.Content | ConvertFrom-Json
    $contentIds = $contentsResponseContent.data.content | ForEach-Object { $_.id }
    Write-Host "Found $($contentIds.Count) content items to delete"
    foreach ($id in $contentIds) {
        try {
            Invoke-WebRequest -Uri "$apiBaseUrl/contents/${id}" -Method DELETE -Headers $headers
            Write-Host "Deleted content with ID: ${id}"
        } catch {
            # Ignore 404 errors if content is already deleted
            if ($_.Exception.Response.StatusCode -ne 404) {
                $errorMessage = $_.Exception.Message
                Write-Host "Failed to delete content with ID ${id}: ${errorMessage}"
            }
        }
    }
} catch {
    $errorMessage = $_.Exception.Message
    Write-Host "Failed to get content list: ${errorMessage}"
}

# Delete all categories
try {
    $categoriesResponse = Invoke-WebRequest -Uri "$apiBaseUrl/categories?page=1&size=100" -Method GET -Headers $headers
    $categoriesResponseContent = $categoriesResponse.Content | ConvertFrom-Json
    $categoryIds = $categoriesResponseContent.data.content | ForEach-Object { $_.id }
    Write-Host "Found $($categoryIds.Count) categories to delete"
    foreach ($id in $categoryIds) {
        try {
            Invoke-WebRequest -Uri "$apiBaseUrl/categories/${id}" -Method DELETE -Headers $headers
            Write-Host "Deleted category with ID: ${id}"
        } catch {
            if ($_.Exception.Response.StatusCode -ne 404) {
                $errorMessage = $_.Exception.Message
                Write-Host "Failed to delete category with ID ${id}: ${errorMessage}"
                if ($_.Exception.Response) {
                    $responseStream = $_.Exception.Response.GetResponseStream()
                    $reader = New-Object System.IO.StreamReader($responseStream)
                    $responseBody = $reader.ReadToEnd()
                    Write-Host "Response body: $responseBody"
                }
            }
        }
    }
} catch {
    $errorMessage = $_.Exception.Message
    Write-Host "Failed to get category list: ${errorMessage}"
    if ($_.Exception.Response) {
        $responseStream = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($responseStream)
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response body: $responseBody"
    }
}

# Delete all sites
try {
    $sitesResponse = Invoke-WebRequest -Uri "$apiBaseUrl/sites?page=1&size=100" -Method GET -Headers $headers
    $sitesResponseContent = $sitesResponse.Content | ConvertFrom-Json
    $siteIds = $sitesResponseContent.data.content | ForEach-Object { $_.id }
    Write-Host "Found $($siteIds.Count) sites to delete"
    foreach ($id in $siteIds) {
        try {
            Invoke-WebRequest -Uri "$apiBaseUrl/sites/${id}" -Method DELETE -Headers $headers
            Write-Host "Deleted site with ID: ${id}"
        } catch {
            if ($_.Exception.Response.StatusCode -ne 404) {
                $errorMessage = $_.Exception.Message
                Write-Host "Failed to delete site with ID ${id}: ${errorMessage}"
            }
        }
    }
} catch {
    $errorMessage = $_.Exception.Message
    Write-Host "Failed to get site list: ${errorMessage}"
}


# Add new sites and capture them
$sitesToAdd = @(
    @{ name = "主站"; code = "main_site"; domain = "main.example.com" },
    @{ name = "子站"; code = "sub_site"; domain = "sub.example.com" }
)
$createdSites = @()
foreach ($site in $sitesToAdd) {
    $siteBody = $site | ConvertTo-Json
    try {
        $newSiteResponse = Invoke-WebRequest -Uri "$apiBaseUrl/sites" -Method POST -Headers $headers -Body $siteBody -SkipHttpErrorCheck
        if ($newSiteResponse.StatusCode -ne 200) {
            Write-Host "Failed to create site $($site.name). Status code: $($newSiteResponse.StatusCode)"
            Write-Host "Response body: $($newSiteResponse.Content)"
        } else {
            $newSite = $newSiteResponse.Content | ConvertFrom-Json
            $createdSites += $newSite.data
            Write-Host "Created site: $($newSite.data.name) (ID: $($newSite.data.id))"

            # Create a default root category for the new site
            $defaultCategory = @{ 
                name = "默认分类"; 
                code = "default"; 
                siteId = $newSite.data.id 
            }
            $categoryBody = $defaultCategory | ConvertTo-Json
            $newCategoryResponse = Invoke-WebRequest -Uri "$apiBaseUrl/categories" -Method POST -Headers $headers -Body $categoryBody -SkipHttpErrorCheck
            if ($newCategoryResponse.StatusCode -ne 200) {
                Write-Host "Failed to create default category for site $($newSite.data.name). Status code: $($newCategoryResponse.StatusCode)"
                Write-Host "Response body: $($newCategoryResponse.Content)"
            } else {
                $newCategory = $newCategoryResponse.Content | ConvertFrom-Json
                Write-Host "Created default category for site: $($newSite.data.name) (ID: $($newCategory.data.id))"
            }
        }
    } catch {
        $errorMessage = $_.Exception.Message
        Write-Host "An unexpected error occurred while creating site $($site.name): ${errorMessage}"
    }
}

# Add new categories and capture them
$categoriesToAdd = @(
    @{ name = "公司动态"; code = "company-news" },
    @{ name = "技术文章"; code = "tech-articles" }
)
$createdCategories = @()
# Assign first category to first site, second to second, etc.
for ($i = 0; $i -lt $categoriesToAdd.Count; $i++) {
    $category = $categoriesToAdd[$i]
    $site = $createdSites[$i]
    Write-Host "Debug: Site object from array: $($site | ConvertTo-Json -Depth 5)"
    Write-Host "Debug: Site ID from array: $($site.id)"
    $category.siteId = $site.id
    $categoryBody = $category | ConvertTo-Json
    try {
        $newCategoryResponse = Invoke-WebRequest -Uri "$apiBaseUrl/categories" -Method POST -Headers $headers -Body $categoryBody -SkipHttpErrorCheck
        if ($newCategoryResponse.StatusCode -ne 200) {
            Write-Host "Failed to create category $($category.name). Status code: $($newCategoryResponse.StatusCode)"
            Write-Host "Response body: $($newCategoryResponse.Content)"
        } else {
            $newCategory = $newCategoryResponse.Content | ConvertFrom-Json
            $createdCategories += $newCategory.data
            Write-Host "Created category: $($newCategory.data.name) (ID: $($newCategory.data.id)) for site $($site.name)"
        }
    } catch {
        $errorMessage = $_.Exception.Message
        Write-Host "An unexpected error occurred while creating category $($category.name): ${errorMessage}"
    }
}

# Add new content in Chinese
$contentToAdd = @(
    @{ title = "联系我们"; contentType = "PAGE"; status = "PUBLISHED"; content = "这是联系我们的页面内容。" },
    @{ title = "关于我们"; contentType = "PAGE"; status = "PUBLISHED"; content = "这是关于我们的页面内容。" },
    @{ title = "React Hooks 深度解析"; contentType = "ARTICLE"; status = "PUBLISHED"; content = "React Hooks 的详细介绍和使用案例。" },
    @{ title = "Node.js 性能优化"; contentType = "ARTICLE"; status = "PUBLISHED"; content = "如何优化 Node.js 应用的性能。" },
    @{ title = "Docker 容器化指南"; contentType = "ARTICLE"; status = "PUBLISHED"; content = "Docker 容器化入门和实践。" },
    @{ title = "TypeScript 高级类型系统"; contentType = "ARTICLE"; status = "PUBLISHED"; content = "TypeScript 高级类型的使用技巧。" }
)

# Assign content to sites and categories
$contentToAdd[0].siteId = $createdSites[0].id
$contentToAdd[0].categoryId = $createdCategories[0].id
$contentToAdd[1].siteId = $createdSites[0].id
$contentToAdd[1].categoryId = $createdCategories[0].id

$contentToAdd[2].siteId = $createdSites[1].id
$contentToAdd[2].categoryId = $createdCategories[1].id
$contentToAdd[3].siteId = $createdSites[1].id
$contentToAdd[3].categoryId = $createdCategories[1].id
$contentToAdd[4].siteId = $createdSites[1].id
$contentToAdd[4].categoryId = $createdCategories[1].id
$contentToAdd[5].siteId = $createdSites[1].id
$contentToAdd[5].categoryId = $createdCategories[1].id

foreach ($content in $contentToAdd) {
    $contentBody = $content | ConvertTo-Json
    try {
        $newContentResponse = Invoke-WebRequest -Uri "$apiBaseUrl/contents" -Method POST -Headers $headers -Body $contentBody -SkipHttpErrorCheck
        if ($newContentResponse.StatusCode -ne 200) {
            Write-Host "Failed to create content $($content.title). Status code: $($newContentResponse.StatusCode)"
            Write-Host "Response body: $($newContentResponse.Content)"
        } else {
            $newContent = $newContentResponse.Content | ConvertFrom-Json
            Write-Host "Created content: $($newContent.data.title) (ID: $($newContent.data.id))"
        }
    } catch {
        $errorMessage = $_.Exception.Message
        Write-Host "An unexpected error occurred while creating content $($content.title): ${errorMessage}"
    }
}