const apiBase = "";
const money = new Intl.NumberFormat("en-LK", { style: "currency", currency: "LKR" });

document.addEventListener("DOMContentLoaded", () => {
    setupLogoutButtons();
    routePage();
    if (window.lucide) {
        lucide.createIcons();
    }
});

function routePage() {
    const page = document.body.dataset.page;
    if (page === "home") initHome();
    if (page === "register") initRegister();
    if (page === "login") initLogin();
    if (page === "dashboard") initDashboard();
    if (page === "admin") initAdmin();
}

async function request(path, options = {}) {
    const response = await fetch(apiBase + path, {
        headers: { "Content-Type": "application/json" },
        ...options
    });
    const text = await response.text();
    const data = text ? JSON.parse(text) : null;

    if (!response.ok) {
        throw new Error(data?.message || "Request failed");
    }

    return data;
}

function value(id) {
    return document.getElementById(id).value.trim();
}

function isValidEmail(email) {
    return /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(email);
}

function isValidPhone(phoneNumber) {
    return /^[0-9]{10}$/.test(phoneNumber);
}

function validateStudentFields({ email, password, phoneNumber }, { passwordRequired = true } = {}) {
    if (email !== undefined && !isValidEmail(email)) {
        throw new Error("Please enter a valid email address");
    }
    if ((passwordRequired || password) && (!password || password.length < 6)) {
        throw new Error("Password must be at least 6 characters");
    }
    if (phoneNumber !== undefined && !isValidPhone(phoneNumber)) {
        throw new Error("Phone number must contain exactly 10 numbers");
    }
}

function showToast(message) {
    const toast = document.getElementById("appToast");
    const toastMessage = document.getElementById("toastMessage");
    if (!toast || !toastMessage) {
        alert(message);
        return;
    }
    toastMessage.textContent = message;
    bootstrap.Toast.getOrCreateInstance(toast, { delay: 3500 }).show();
}

function setupLogoutButtons() {
    document.querySelectorAll("[data-logout]").forEach(button => {
        button.addEventListener("click", () => {
            localStorage.removeItem(button.dataset.logout);
            window.location.href = button.dataset.redirect || "/";
        });
    });
}

function subjectsToList(subjects) {
    if (!subjects) return [];
    return subjects.split(",").map(subject => subject.trim()).filter(Boolean);
}

function renderCourseCard(course, actions = true) {
    const subjectHtml = subjectsToList(course.subjects).slice(0, 3)
        .map(subject => `<span class="chip">${subject}</span>`)
        .join("");

    return `
        <article class="course-card">
            <div class="course-code">${course.courseCode}</div>
            <h3 class="h5 fw-bold">${course.title}</h3>
            <p class="course-description">${course.description || ""}</p>
            <div class="chip-row mb-3">
                <span class="chip">${course.duration || "Flexible"}</span>
                <span class="chip">${course.capacity} seats</span>
                ${subjectHtml}
            </div>
            <div class="mt-auto d-flex align-items-center justify-content-between gap-3">
                <div class="price">${money.format(course.price)}</div>
                ${actions ? `
                    <div class="d-inline-flex gap-2">
                        <button class="btn btn-outline-brand" type="button" onclick="openCourseDetails(${course.id})">
                            <i data-lucide="eye"></i>
                            View
                        </button>
                        <a class="btn btn-brand" href="/register/?courseId=${course.id}">
                            <i data-lucide="user-plus"></i>
                            Register
                        </a>
                    </div>
                ` : ""}
            </div>
        </article>
    `;
}

async function initHome() {
    try {
        const courses = await request("/api/courses");
        window.courseCache = courses;
        document.getElementById("courseList").innerHTML = courses
            .filter(course => course.active)
            .map(course => `<div class="col-md-6 col-xl-4">${renderCourseCard(course)}</div>`)
            .join("");
        document.getElementById("courseCount").textContent = courses.length;
        if (window.lucide) lucide.createIcons();
    } catch (error) {
        showToast(error.message);
    }
}

function openCourseDetails(courseId) {
    const course = (window.courseCache || []).find(item => item.id === courseId);
    if (!course) return;

    const subjects = subjectsToList(course.subjects)
        .map(subject => `<li>${subject}</li>`)
        .join("");

    document.getElementById("courseModalTitle").textContent = course.title;
    document.getElementById("courseModalBody").innerHTML = `
        <p class="text-muted">${course.description || ""}</p>
        <div class="row g-3 mb-3">
            <div class="col-sm-4"><div class="metric-card"><span>Code</span><strong class="fs-5">${course.courseCode}</strong></div></div>
            <div class="col-sm-4"><div class="metric-card"><span>Duration</span><strong class="fs-5">${course.duration || "-"}</strong></div></div>
            <div class="col-sm-4"><div class="metric-card"><span>Price</span><strong class="fs-5">${money.format(course.price)}</strong></div></div>
        </div>
        <h3 class="h6 fw-bold">Subjects</h3>
        <ul>${subjects || "<li>Subjects will be updated soon.</li>"}</ul>
    `;
    document.getElementById("courseModalRegister").href = `/register/?courseId=${course.id}`;
    bootstrap.Modal.getOrCreateInstance(document.getElementById("courseModal")).show();
}

async function initRegister() {
    const select = document.getElementById("courseId");
    const params = new URLSearchParams(window.location.search);
    const student = JSON.parse(localStorage.getItem("studentUser") || "null");
    const existingStudentMode = student && params.get("studentId") === String(student.id);

    if (existingStudentMode) {
        document.getElementById("fullName").value = student.name;
        document.getElementById("email").value = student.email;
        document.getElementById("password").required = false;
        document.getElementById("age").required = false;
        document.getElementById("fullName").disabled = true;
        document.getElementById("email").disabled = true;
        document.getElementById("password").disabled = true;
        document.getElementById("age").disabled = true;
        document.getElementById("phoneNumber").disabled = true;
        document.getElementById("phoneNumber").required = false;
        document.getElementById("existingStudentName").textContent = `${student.name} (${student.email})`;
        document.getElementById("existingStudentNotice").classList.remove("d-none");
        document.getElementById("registerSubmitBtn").innerHTML = '<i data-lucide="credit-card"></i> Register Another Course';
        if (window.lucide) lucide.createIcons();
    }

    try {
        const courses = (await request("/api/courses")).filter(course => course.active);
        select.innerHTML = `<option value="">Select a course</option>` + courses
            .map(course => `<option value="${course.id}">${course.courseCode} - ${course.title}</option>`)
            .join("");

        if (params.get("courseId")) {
            select.value = params.get("courseId");
        }

        const updateSelectedCourse = () => {
            const selected = courses.find(course => String(course.id) === select.value);
            document.getElementById("selectedCourseSummary").innerHTML = selected
                ? renderSelectedCourse(selected)
                : "<span class='text-muted'>Select a course to see price and subjects.</span>";
        };

        select.addEventListener("change", updateSelectedCourse);
        updateSelectedCourse();
    } catch (error) {
        showToast(error.message);
    }

    document.getElementById("studentRegisterForm").addEventListener("submit", registerStudent);
}

function renderSelectedCourse(course) {
    const subjects = subjectsToList(course.subjects).map(subject => `<span class="chip">${subject}</span>`).join("");
    return `
        <div class="d-flex justify-content-between align-items-start gap-3">
            <div>
                <strong>${course.title}</strong>
                <div class="text-muted small">${course.courseCode} - ${course.duration || "Flexible"}</div>
            </div>
            <div class="price fs-5">${money.format(course.price)}</div>
        </div>
        <div class="chip-row mt-3">${subjects}</div>
    `;
}

async function registerStudent(event) {
    event.preventDefault();
    const student = JSON.parse(localStorage.getItem("studentUser") || "null");
    const params = new URLSearchParams(window.location.search);
    const existingStudentMode = student && params.get("studentId") === String(student.id);

    if (existingStudentMode) {
        const payload = {
            courseId: Number(value("courseId")),
            paymentMethod: value("paymentMethod"),
            cardHolderName: value("cardHolderName")
        };

        try {
            const result = await request(`/api/students/${student.id}/courses/register`, {
                method: "POST",
                body: JSON.stringify(payload)
            });
            showToast(`${result.message}. Ref: ${result.transactionReference}`);
            setTimeout(() => window.location.href = "/dashboard/", 900);
        } catch (error) {
            showToast(error.message);
        }
        return;
    }

    const payload = {
        fullName: value("fullName"),
        email: value("email"),
        password: value("password"),
        phoneNumber: value("phoneNumber"),
        age: Number(value("age")),
        courseId: Number(value("courseId")),
        paymentMethod: value("paymentMethod"),
        cardHolderName: value("cardHolderName")
    };

    try {
        validateStudentFields(payload);
        const result = await request("/api/students/register", {
            method: "POST",
            body: JSON.stringify(payload)
        });
        showToast(`${result.message}. Ref: ${result.transactionReference}`);
        setTimeout(() => window.location.href = "/login/", 900);
    } catch (error) {
        showToast(error.message);
    }
}

function initLogin() {
    document.getElementById("studentLoginForm").addEventListener("submit", async event => {
        event.preventDefault();
        try {
            validateStudentFields({
                email: value("email"),
                password: value("password")
            });
            const student = await request("/api/students/login", {
                method: "POST",
                body: JSON.stringify({
                    email: value("email"),
                    password: value("password")
                })
            });
            localStorage.setItem("studentUser", JSON.stringify(student));
            window.location.href = "/dashboard/";
        } catch (error) {
            showToast(error.message);
        }
    });
}

async function initDashboard() {
    let student = JSON.parse(localStorage.getItem("studentUser") || "null");
    if (!student) {
        window.location.href = "/login/";
        return;
    }

    document.getElementById("newCourseLink").href = `/register/?studentId=${student.id}`;
    document.getElementById("studentProfileForm").addEventListener("submit", saveStudentProfile);

    try {
        const profile = await request(`/api/students/${student.id}`);
        student = { ...student, name: profile.fullName, email: profile.email, phoneNumber: profile.phoneNumber || "" };
        localStorage.setItem("studentUser", JSON.stringify(student));
        renderStudentProfile(student);

        const enrollments = await request(`/api/enrollments/student/${student.id}`);
        document.getElementById("courseMetric").textContent = enrollments.length;
        document.getElementById("paidMetric").textContent = money.format(
            enrollments.reduce((total, item) => total + Number(item.payment.amount), 0)
        );
        document.getElementById("enrollmentList").innerHTML = enrollments.map(item => {
            const subjects = subjectsToList(item.course.subjects).map(subject => `<span class="chip">${subject}</span>`).join("");
            return `
                <div class="lms-item">
                    <div class="d-flex flex-wrap justify-content-between gap-3">
                        <div>
                            <div class="course-code">${item.course.courseCode}</div>
                            <h3 class="h5 fw-bold">${item.course.title}</h3>
                            <p class="text-muted">${item.course.description || ""}</p>
                        </div>
                        <div class="text-end">
                            <div class="price fs-5">${money.format(item.payment.amount)}</div>
                            <span class="pill">${item.payment.status}</span>
                        </div>
                    </div>
                    <div class="chip-row mt-3">${subjects}</div>
                    <div class="profile-strip mt-3">
                        <span>Transaction</span>
                        <strong>${item.payment.transactionReference}</strong>
                    </div>
                </div>
            `;
        }).join("") || "<div class='panel text-muted'>No courses registered yet.</div>";
    } catch (error) {
        showToast(error.message);
    }
}

function renderStudentProfile(student) {
    document.getElementById("studentName").textContent = student.name;
    document.getElementById("studentEmail").textContent = student.email;
    document.getElementById("profileFullName").value = student.name || "";
    document.getElementById("profileEmail").value = student.email || "";
    document.getElementById("profilePhoneNumber").value = student.phoneNumber || "";
    document.getElementById("profilePassword").value = "";
}

async function saveStudentProfile(event) {
    event.preventDefault();
    const student = JSON.parse(localStorage.getItem("studentUser") || "null");
    if (!student) return;

    const payload = {
        fullName: value("profileFullName"),
        phoneNumber: value("profilePhoneNumber"),
        password: value("profilePassword")
    };

    try {
        validateStudentFields({
            password: payload.password,
            phoneNumber: payload.phoneNumber
        }, { passwordRequired: false });
        const profile = await request(`/api/students/${student.id}/profile`, {
            method: "PUT",
            body: JSON.stringify(payload)
        });
        const updatedStudent = {
            ...student,
            name: profile.fullName,
            email: profile.email,
            phoneNumber: profile.phoneNumber || ""
        };
        localStorage.setItem("studentUser", JSON.stringify(updatedStudent));
        renderStudentProfile(updatedStudent);
        showToast("Profile updated successfully");
    } catch (error) {
        showToast(error.message);
    }
}

function initAdmin() {
    const admin = JSON.parse(localStorage.getItem("adminUser") || "null");
    if (admin) {
        showAdminDashboard(admin);
    }

    document.getElementById("adminLoginForm").addEventListener("submit", async event => {
        event.preventDefault();
        try {
            const result = await request("/api/admins/login", {
                method: "POST",
                body: JSON.stringify({
                    email: value("adminEmail"),
                    password: value("adminPassword")
                })
            });
            localStorage.setItem("adminUser", JSON.stringify(result));
            showAdminDashboard(result);
        } catch (error) {
            showToast(error.message);
        }
    });

    document.getElementById("courseForm").addEventListener("submit", saveCourse);
    document.getElementById("clearCourseBtn").addEventListener("click", clearCourseForm);
    document.getElementById("adminRegisterForm").addEventListener("submit", registerAdmin);
}

async function showAdminDashboard(admin) {
    document.getElementById("adminApp").classList.add("dash-mode");
    document.getElementById("adminName").textContent = admin.name;
    await loadAdminCourses();
    await loadAdminDashboardData();  // NEW
    await loadAdminStudents();        // NEW
    await loadAdminTransactions();    // NEW
}

// NEW FUNCTION: Load dashboard summary
async function loadAdminDashboardData() {
    try {
        const summary = await request("/api/admin/dashboard/summary");
        document.getElementById("adminStudentCount").textContent = summary.totalStudents;
        document.getElementById("adminRevenue").textContent = money.format(summary.totalRevenue);
    } catch (error) {
        console.error("Failed to load dashboard data:", error);
    }
}

// NEW FUNCTION: Load students list
async function loadAdminStudents() {
    try {
        const students = await request("/api/admin/dashboard/students");
        document.getElementById("adminStudentTable").innerHTML = students.map(student => `
            <tr>
                <td>${escapeHtml(student.fullName)}</td>
                <td>${escapeHtml(student.email)}</td>
                <td>${student.enrolledCoursesCount}</td>
                <td>${money.format(student.totalPaid)}</td>
            </tr>
        `).join("");
    } catch (error) {
        console.error("Failed to load students:", error);
    }
}

// NEW FUNCTION: Load transactions list
async function loadAdminTransactions() {
    try {
        const transactions = await request("/api/admin/dashboard/transactions/recent");
        document.getElementById("adminTransactionTable").innerHTML = transactions.map(tx => `
            <tr>
                <td><small>${tx.transactionReference}</small></td>
                <td>${escapeHtml(tx.studentName)}</td>
                <td>${money.format(tx.amount)}</td>
                <td><span class="pill ${tx.status === 'PAID' ? 'bg-success text-white' : 'bg-warning'}">${tx.status}</span></td>
            </tr>
        `).join("");
    } catch (error) {
        console.error("Failed to load transactions:", error);
    }
}

// Helper function to escape HTML (prevent XSS)
function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>]/g, function(m) {
        if (m === '&') return '&amp;';
        if (m === '<') return '&lt;';
        if (m === '>') return '&gt;';
        return m;
    });
}

async function loadAdminCourses() {
    try {
        const courses = await request("/api/courses");
        window.adminCourseCache = courses;
        document.getElementById("adminCourseCount").textContent = courses.length;
        document.getElementById("courseTable").innerHTML = courses.map(course => `
            <tr>
                <td class="fw-bold">${course.courseCode}</td>
                <td>${course.title}</td>
                <td>${money.format(course.price)}</td>
                <td>${course.active ? "Active" : "Closed"}</td>
                <td class="text-end">
                    <div class="d-inline-flex gap-2">
                        <button class="btn btn-sm btn-outline-brand" type="button" onclick="editCourse(${course.id})">
                            <i data-lucide="pencil"></i>
                        </button>
                        <button class="btn btn-sm btn-soft" type="button" onclick="deleteCourse(${course.id})">
                            <i data-lucide="trash-2"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `).join("");
        if (window.lucide) lucide.createIcons();
    } catch (error) {
        showToast(error.message);
    }
}

async function saveCourse(event) {
    event.preventDefault();
    const courseId = value("courseId");
    const payload = {
        courseCode: value("courseCode"),
        title: value("courseTitle"),
        description: value("courseDescription"),
        subjects: value("courseSubjects"),
        duration: value("courseDuration"),
        capacity: Number(value("courseCapacity")),
        price: Number(value("coursePrice")),
        active: document.getElementById("courseActive").checked
    };

    try {
        await request(courseId ? `/api/courses/${courseId}` : "/api/courses", {
            method: courseId ? "PUT" : "POST",
            body: JSON.stringify(payload)
        });
        showToast(courseId ? "Course updated" : "Course created");
        clearCourseForm();
        await loadAdminCourses();
    } catch (error) {
        showToast(error.message);
    }
}

function editCourse(courseId) {
    const course = (window.adminCourseCache || []).find(item => item.id === courseId);
    if (!course) return;

    document.getElementById("courseId").value = course.id;
    document.getElementById("courseCode").value = course.courseCode;
    document.getElementById("courseTitle").value = course.title;
    document.getElementById("courseDescription").value = course.description || "";
    document.getElementById("courseSubjects").value = course.subjects || "";
    document.getElementById("courseDuration").value = course.duration || "";
    document.getElementById("courseCapacity").value = course.capacity;
    document.getElementById("coursePrice").value = course.price;
    document.getElementById("courseActive").checked = course.active;
    document.getElementById("saveCourseBtn").innerHTML = '<i data-lucide="save"></i> Update Course';
    if (window.lucide) lucide.createIcons();
}

async function deleteCourse(courseId) {
    if (!confirm("Delete this course?")) return;
    try {
        await request(`/api/courses/${courseId}`, { method: "DELETE" });
        showToast("Course deleted");
        await loadAdminCourses();
    } catch (error) {
        showToast(error.message);
    }
}

function clearCourseForm() {
    document.getElementById("courseForm").reset();
    document.getElementById("courseId").value = "";
    document.getElementById("courseActive").checked = true;
    document.getElementById("saveCourseBtn").innerHTML = '<i data-lucide="save"></i> Save Course';
    if (window.lucide) lucide.createIcons();
}

async function registerAdmin(event) {
    event.preventDefault();
    const currentAdmin = JSON.parse(localStorage.getItem("adminUser") || "null");
    try {
        const result = await request("/api/admins/register", {
            method: "POST",
            body: JSON.stringify({
                name: value("newAdminName"),
                email: value("newAdminEmail"),
                password: value("newAdminPassword"),
                creatorAdminId: currentAdmin?.id
            })
        });
        showToast(result.message);
        event.target.reset();
    } catch (error) {
        showToast(error.message);
    }
}
