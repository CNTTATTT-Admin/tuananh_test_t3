# tuananh_test_t3
Hệ thống kiểm tra phát hiện đạo văn checkplagiarism

Một hệ thống phát hiện đạo văn toàn diện được xây dựng trên bộ khung **React (Vite), Spring Boot, MySQL và Elasticsearch**. Hệ thống này hỗ trợ sinh viên, giảng viên và ban quản trị nhà trường trong việc quản lý lớp học, nộp bài tập và ngay lập tức phát hiện tỷ lệ đạo văn thông qua công nghệ phân tích và đối chiếu văn bản siêu tốc độ của Elasticsearch.

![Dashboard Preview](https://via.placeholder.com/800x400.png?text=Smart+Plagiarism+Dashboard)

---

## 🌟 Các Tính Năng Nổi Bật

*   **🛡️ Phân quyền Đa Tầng**: Giao diện và quyền hạn hoàn toàn độc lập giữa **Giảng Viên/Quản Trị** (Tạo lớp, giám sát đạo văn, duyệt sinh viên) và **Sinh Viên** (Xin vào lớp, nộp bài tự luận/tài liệu).
*   **📊 Trung Tâm Phân Tích Dữ Liệu**: Thu thập và trực quan hoá các số liệu tăng trưởng, lượng truy vấn, và tỷ lệ rủi ro bằng biểu đồ tròn & biểu đồ phổ.
*   **🔍 Lõi Quét Elasticsearch**: Tự động đánh số, chia nhỏ tài liệu và truy vấn các đoạn văn bản có mức độ tương đồng một cách siêu tốc.
*   **📈 Thang Đo Đạo Văn Tuỳ Chỉnh**: Cho phép giảng viên tuỳ biến thước đo mức độ nguy hiểm (An toàn, Thấp, Trung bình, Nguy hiểm) ứng với từng màu sắc cảnh báo trong bài làm của sinh viên.
*   **🐳 Hỗ trợ Docker (Deploy)**: Khởi động lập tức chuỗi dây chuyền MySQL, Elasticsearch, và Spring Boot API chỉ với một lệnh duy nhất.

---

## 💻 Công Nghệ Sử Dụng

### Frontend (Giao diện người dùng)
*   **React 18** + **TypeScript** 
*   **Vite** (Trình biên dịch Front-end tốc độ cao)
*   **Tailwind CSS** (Hệ thống CSS đa năng)
*   **Recharts** (Vẽ biểu đồ và thống kê)
*   **Axios** 

### Backend (Hệ thống cốt lõi)
*   **Java 17** + **Spring Boot 3**
*   **Spring Security + JWT** (Chứng thực đa lớp)
*   **Spring Data JPA** 
*   **Elasticsearch 8** (Nền tảng vector hoá và đối chiếu văn bản)
*   **MySQL 8.0** 

---

## 🚀 Hướng Dẫn Cài Đặt và Khởi Chạy

### 👉 Cách 1: Sử dụng Docker (Khuyên dùng)
Bạn không cần phải cài Java, MySQL hay Elasticsearch rắc rối lên máy tính. Hãy dùng Docker để nó tự động lo hết!
**Yêu cầu:** Máy tính đã cài đặt [Docker & Docker Compose](https://www.docker.com/).

1.  Mở terminal (hoặc cmd) tại thư mục chứa mã nguồn (`plagiarism/`).
2.  Chạy lệnh ma thuật sau:
    ```bash
    docker-compose up -d --build
    ```
3.  Uống một cốc cà phê. Docker sẽ tự động tải các hệ quản trị database và biên dịch Java. Sau 3-5 phút, API backend của bạn đã chễm chệ ở cổng `http://localhost:8080/api/v1`.

### 👉 Cách 2: Chạy Thủ Công (Dành cho Coder)

**1. Khởi chạy Backend**
*   Nửa kia của ứng dụng cần **MySQL** (Port `3306`) và **Elasticsearch** (Port `9200`) đã sẵn sàng dưới máy của bạn.
*   Tạo một CSDL tên là `plagiarism` với mật khẩu root là `123456`.
*   Mở terminal chui vào thư mục `backend/` và gõ:
    ```bash
    ./mvnw spring-boot:run
    ```

**2. Khởi chạy Frontend**
*   Mở một cửa sổ terminal khác chui vào thư mục `frontend/`.
*   Tải thư viện NPM:
    ```bash
    npm install
    ```
*   Tạo file `.env` từ `.env.template` (Đã lưu sẵn cấu hình mặc định là `VITE_API_URL=http://localhost:8080/api/v1`)
*   Bật server:
    ```bash
    npm run dev
    ```
*   Bay ngay vào trình duyệt và chiêm ngưỡng: `http://localhost:5173`

---

