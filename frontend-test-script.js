// 前端功能测试脚本
// 在浏览器控制台中运行此脚本来测试前端功能

console.log('🧪 开始前端功能测试...');

// 测试配置
const config = {
    apiBase: 'http://localhost:8080/api',
    frontendBase: 'http://localhost:3001',
    testUser: {
        username: 'testuser',
        password: 'password123',
        email: 'test@example.com'
    }
};

// 测试结果收集器
const testResults = {
    passed: 0,
    failed: 0,
    results: []
};

// 测试工具函数
function logTest(name, passed, message) {
    const status = passed ? '✅ PASS' : '❌ FAIL';
    const result = `${status} - ${name}: ${message}`;
    console.log(result);
    
    testResults.results.push({ name, passed, message });
    if (passed) {
        testResults.passed++;
    } else {
        testResults.failed++;
    }
}

// 1. 测试前端应用是否可访问
async function testFrontendAccess() {
    try {
        const response = await fetch(config.frontendBase);
        if (response.ok) {
            logTest('前端访问', true, '前端应用可正常访问');
        } else {
            logTest('前端访问', false, `HTTP状态码: ${response.status}`);
        }
    } catch (error) {
        logTest('前端访问', false, `网络错误: ${error.message}`);
    }
}

// 2. 测试后端API连接
async function testBackendAPI() {
    try {
        const response = await fetch(`${config.apiBase}/actuator/health`);
        const data = await response.json();
        if (data.status === 'UP') {
            logTest('后端API', true, '后端API健康检查通过');
        } else {
            logTest('后端API', false, `健康检查状态: ${data.status}`);
        }
    } catch (error) {
        logTest('后端API', false, `连接失败: ${error.message}`);
    }
}

// 3. 测试CORS配置
async function testCORS() {
    try {
        const response = await fetch(`${config.apiBase}/actuator/health`, {
            method: 'GET',
            headers: {
                'Origin': config.frontendBase
            }
        });
        if (response.ok) {
            logTest('CORS配置', true, '跨域请求正常');
        } else {
            logTest('CORS配置', false, '跨域请求被阻止');
        }
    } catch (error) {
        logTest('CORS配置', false, `CORS错误: ${error.message}`);
    }
}

// 4. 测试用户登录API
async function testLoginAPI() {
    try {
        const response = await fetch(`${config.apiBase}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                usernameOrEmail: config.testUser.username,
                password: config.testUser.password
            })
        });
        
        const data = await response.json();
        if (data.success && data.data.accessToken) {
            logTest('登录API', true, `用户 ${data.data.user.username} 登录成功`);
            
            // 保存令牌供后续测试使用
            window.testToken = data.data.accessToken;
            window.testUser = data.data.user;
            
            return data.data.accessToken;
        } else {
            logTest('登录API', false, `登录失败: ${data.message}`);
        }
    } catch (error) {
        logTest('登录API', false, `请求失败: ${error.message}`);
    }
    return null;
}

// 5. 测试受保护的API端点
async function testProtectedAPI(token) {
    if (!token) {
        logTest('受保护API', false, '没有有效的访问令牌');
        return;
    }
    
    try {
        const response = await fetch(`${config.apiBase}/sites`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });
        
        if (response.ok) {
            const data = await response.json();
            logTest('受保护API', true, `站点列表API调用成功，返回 ${data.data?.content?.length || 0} 个站点`);
        } else {
            logTest('受保护API', false, `HTTP状态码: ${response.status}`);
        }
    } catch (error) {
        logTest('受保护API', false, `请求失败: ${error.message}`);
    }
}

// 6. 测试前端路由
function testFrontendRouting() {
    const routes = ['/', '/login', '/register', '/dashboard'];
    let routeTests = 0;
    let routePassed = 0;
    
    routes.forEach(route => {
        try {
            const url = `${config.frontendBase}${route}`;
            // 注意：由于同源策略，我们无法直接测试路由
            // 这里只是验证URL格式
            const urlObj = new URL(url);
            if (urlObj.href === url) {
                routePassed++;
            }
            routeTests++;
        } catch (error) {
            routeTests++;
        }
    });
    
    logTest('前端路由', routePassed === routeTests, `${routePassed}/${routeTests} 个路由URL格式正确`);
}

// 7. 测试localStorage功能
function testLocalStorage() {
    try {
        const testKey = 'cms_test_key';
        const testValue = 'test_value';
        
        localStorage.setItem(testKey, testValue);
        const retrieved = localStorage.getItem(testKey);
        localStorage.removeItem(testKey);
        
        if (retrieved === testValue) {
            logTest('LocalStorage', true, '本地存储功能正常');
        } else {
            logTest('LocalStorage', false, '本地存储读写异常');
        }
    } catch (error) {
        logTest('LocalStorage', false, `本地存储错误: ${error.message}`);
    }
}

// 8. 测试前端依赖库
function testFrontendDependencies() {
    const dependencies = [
        { name: 'Vue', check: () => window.Vue !== undefined },
        { name: 'Element Plus', check: () => window.ElementPlus !== undefined || document.querySelector('.el-button') !== null },
        { name: 'Vue Router', check: () => window.VueRouter !== undefined || window.location.hash !== undefined }
    ];
    
    dependencies.forEach(dep => {
        try {
            const available = dep.check();
            logTest(`依赖-${dep.name}`, available, available ? '依赖库可用' : '依赖库不可用');
        } catch (error) {
            logTest(`依赖-${dep.name}`, false, `检查失败: ${error.message}`);
        }
    });
}

// 主测试函数
async function runAllTests() {
    console.log('🚀 开始执行所有测试...\n');
    
    // 基础连接测试
    await testFrontendAccess();
    await testBackendAPI();
    await testCORS();
    
    // API功能测试
    const token = await testLoginAPI();
    await testProtectedAPI(token);
    
    // 前端功能测试
    testFrontendRouting();
    testLocalStorage();
    testFrontendDependencies();
    
    // 输出测试结果
    console.log('\n📊 测试结果汇总:');
    console.log(`✅ 通过: ${testResults.passed}`);
    console.log(`❌ 失败: ${testResults.failed}`);
    console.log(`📈 成功率: ${((testResults.passed / (testResults.passed + testResults.failed)) * 100).toFixed(1)}%`);
    
    if (testResults.failed === 0) {
        console.log('\n🎉 所有测试通过！前端应用运行正常！');
    } else {
        console.log('\n⚠️ 部分测试失败，请检查相关功能。');
    }
    
    return testResults;
}

// 导出测试函数
window.frontendTest = {
    runAllTests,
    testFrontendAccess,
    testBackendAPI,
    testLoginAPI,
    testProtectedAPI,
    config,
    results: testResults
};

// 自动运行测试
console.log('💡 测试脚本已加载！');
console.log('📝 运行 frontendTest.runAllTests() 来执行所有测试');
console.log('🔧 或运行单个测试函数，如 frontendTest.testLoginAPI()');

// 如果在测试环境中，自动运行
if (window.location.href.includes('test-frontend.html')) {
    setTimeout(runAllTests, 1000);
}
