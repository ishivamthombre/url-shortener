Designing a URL Shortener like bit.ly is a common System Design question asked in mid to senior-level software engineering interviews. Let's break it down step-by-step, covering hashing, database design, collision handling, scalability, and redirection latency, so you can explain it clearly and confidently in interviews.

🧩 1. Problem Statement
Design a system that takes a long URL (e.g., https://www.example.com/abc/xyz/long-page) and returns a shortened version (e.g., https://bit.ly/abc123). When a user accesses the short URL, they should be redirected to the original long URL.

🧠 2. Functional Requirements
Given a long URL, return a short URL.

Given a short URL, redirect to the original long URL.

Analytics (optional): Track number of redirects.

Expiry (optional): Allow setting expiration time for short URLs.

🚫 3. Non-Functional Requirements
High availability and low latency.

Scalability: Should support billions of URLs and redirections.

Redundancy and fault tolerance.

Minimal latency in redirection (<10ms preferred).

🧮 4. Hashing (Short URL Generation)
Option 1: Base62 Encoding (Preferred)
Use a global auto-incrementing ID (e.g., 12345) and encode it into Base62 (abc123) using characters 0-9, a-z, A-Z (62 characters total).

Why Base62?

Compact

Easy to decode

URL-safe

Example:

java
Copy
Edit
long id = 125;
String shortCode = encodeBase62(id); // returns "cb"
Option 2: Hashing the URL (e.g., MD5, SHA-256)
Apply hash function, then truncate (e.g., take first 6 characters).

Problems:

Hash collisions possible.

Not idempotent if URL is very similar.

Option 3: Custom Key from User
Allow custom aliases (e.g., bit.ly/java8tips) - optional feature.

🧊 5. Collision Handling
If two different URLs result in the same hash or short code, you must resolve this:

Techniques:
Use Base62 encoding on DB ID → No collisions.

If using hashing:

Check DB if the code exists.

If yes, rehash with salt/random string.

Limit retries (e.g., 3 attempts).

🛢️ 6. Database Design
Table: urls
Field	Type	Description
id	BIGINT	Auto-incremented ID
long_url	TEXT	Original long URL
short_code	VARCHAR(10)	Shortened code (e.g., "abc123")
created_at	TIMESTAMP	Time of creation
expires_at	TIMESTAMP	Expiration time (optional)
redirect_count	INT	How many times redirected (optional)

Indexing:
Index on short_code (for fast redirection lookup).

Optional index on created_at or expires_at.

📶 7. Scalability Considerations
a. Read-Heavy System
Redirection (GET short URL) is 90% of the traffic.

Use caching (e.g., Redis) for fast redirects.

b. Write Path
User requests shortening → DB insert → return short URL.

c. Data Partitioning (Sharding)
Partition urls table by hash of short code or ID range.

d. Load Balancer
Distribute traffic among multiple servers.

e. Stateless App Servers
App servers should be stateless and horizontally scalable.

🔁 8. Redirection Flow
Step-by-step:
User hits bit.ly/abc123

Service checks Redis (cache):

If found: return 301 redirect to long URL.

If not: check DB, update cache, redirect.

Redirection HTTP Code:
301 (Permanent Redirect): Safe for SEO

302 (Temporary Redirect): If URL may change

🚀 9. Performance Optimizations
Use Redis/Memcached to store most-used short URLs.

Use CDNs for static redirection pages.

Batch processing for analytics (redirect counts).

Pre-warm cache for hot URLs.

🔐 10. Security Considerations
Prevent abuse: Rate-limiting, CAPTCHA for anonymous users.

URL validation: Prevent malicious URLs (phishing sites).

Expiration: Allow users to set expiry on short URLs.

🔧 11. Tech Stack Suggestion
Component	Tech Example
Language	Java, Node.js, Go
API Framework	Spring Boot / Express.js
DB	PostgreSQL / MySQL
Cache	Redis
ID Generator	Snowflake / DB Auto-Increment
CDN	Cloudflare / AWS CloudFront
Hosting	AWS / GCP / Azure

🧠 12. Sample API Design
1. Shorten URL
   http
   Copy
   Edit
   POST /shorten
   Body:
   {
   "longUrl": "https://www.example.com/very/long/url"
   }
   Response:
   {
   "shortUrl": "https://bit.ly/abc123"
   }
2. Redirect
   http
   Copy
   Edit
   GET /abc123
   → 301 redirect to long URL
   🛠️ 13. Extra Features (if asked)
   Analytics (clicks per URL).

User authentication (save history).

Custom alias support.

URL expiration and renewal.

🎯 Summary to Tell Interviewer
To design a scalable URL shortener, I use Base62 encoding of a unique DB ID for short code generation to avoid collisions. The urls table stores mappings with indexes on short_code. A cache layer like Redis speeds up redirection. The system is read-heavy, so I prioritize read scalability using caching and stateless servers behind a load balancer. I ensure idempotent shortening, handle hash collisions carefully, and support advanced features like custom aliases, analytics, and expiration. I also focus on security and performance to ensure low-latency redirections at scale.