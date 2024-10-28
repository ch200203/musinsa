## 구현 범위

## 고객이 접근 가능한 API

**CustomProductController.class**

- 카테고리별 최저가 상품 조회: 모든 카테고리에 대해 최저가 상품과 브랜드, 가격 정보를 제공하며, 총액을 계산합니다.
- 단일 브랜드 최저가 조회: 단일 브랜드에서 모든 카테고리를 최저가로 구매할 때의 정보를 제공하며, 최저가 브랜드의 카테고리별 가격과 총액을 조회합니다.
- 특정 카테고리 최저/최고가 조회: 특정 카테고리에 대해 최저가와 최고가 브랜드, 가격 정보를 제공합니다.

### API 상세 정보

1. **카테고리별 최저가 상품 조회**
    - **URL**: `/api/customer/products/lowest-price-by-category`
    - **HTTP Method**: `GET`
    - **요청값**: 없음
    - **반환값**:
        ```json
        {
          "totalPrice": "34,100",
          "lowestPrices": [
            {"category": "상의", "brand": "C", "price": "10,000"},
            {"category": "아우터", "brand": "E", "price": "5,000"},
            {"category": "바지", "brand": "D", "price": "3,000"},
            {"category": "스니커즈", "brand": "G", "price": "9,000"},
            {"category": "가방", "brand": "A", "price": "2,000"},
            {"category": "모자", "brand": "D", "price": "1,500"},
            {"category": "양말", "brand": "I", "price": "1,700"},
            {"category": "액세서리", "brand": "F", "price": "1,900"}
          ]
        }
        ```
2. **단일 브랜드 최저가 조회**
    - **URL**: `/api/customer/products/lowest-price-by-brand`
    - **HTTP Method**: `GET`
    - **요청값**: 없음
    - **반환값**:
        ```json
        {
          "brand": "D",
          "lowestCategoryPrices": [
            {"category": "상의", "price": "10,100"},
            {"category": "아우터", "price": "5,100"},
            {"category": "바지", "price": "3,000"},
            {"category": "스니커즈", "price": "9,500"},
            {"category": "가방", "price": "2,500"},
            {"category": "모자", "price": "1,500"},
            {"category": "양말", "price": "2,400"},
            {"category": "액세서리", "price": "2,000"}
          ],
          "totalPrice": "36,100"
        }
        ```
3. **특정 카테고리 최저/최고가 조회**
    - **URL**: `/api/customer/products/price-info/{categoryName}`
    - **HTTP Method**: `GET`
    - **요청값**:
        - `categoryName` (Path Variable): 조회할 카테고리 이름 (예: `상의`, `바지`)
    - **반환값**:
        ```json
        {
          "category": "상의",
          "lowestPrices": [
            {"brand": "C", "price": "10,000"}
          ],
          "highestPrices": [
            {"brand": "F", "price": "11,200"}
          ]
        }
        ```

## 운영자가 접근 가능한 API

**AdminProductController.class**

- 상품 생성: 새 상품을 등록합니다. 상품명, 브랜드, 카테고리 등의 정보를 포함하여 상품을 생성합니다.
- 상품 업데이트: 기존 상품의 정보를 업데이트합니다. 상품 ID를 통해 특정 상품을 식별하고, 업데이트된 정보를 반영합니다.
- 상품 삭제: 특정 상품을 삭제합니다. 상품 ID를 통해 상품을 식별하고 해당 상품을 삭제 처리합니다.

### **API 경로 및 설명**

1. **상품 생성**
    - **URL**: `/api/admin/product`
    - **HTTP Method**: `POST`
    - **요청값**:
        ```json
            {
              "brandId": "long",         // 브랜드 ID
              "category": "string",      // 카테고리
              "productName": "string",   // 상품명
              "price": "decimal"         // 상품 가격
            }
        ```
   - **반환값**:
       ```json
       {
         "statusCode": 201,
         "message": "상품 생성 성공",
         "data" : {
             "id" : 1
         }
       }
       ```
2. **상품 업데이트**
   - **URL**: `/api/admin/product/{productId}`
   - **HTTP Method**: `PATCH`
     - **요청값**:
       - `productId` (Path Variable): 조회할 상품 Id
     ```json
        {
            "newName": "string",      // 업데이트할 상품명 (선택 사항)
            "newPrice": "decimal"     // 업데이트할 상품 가격 (선택 사항)
        }
      ```
         
   - **반환값**:
     ```json
        {
             "statusCode": 200,
             "message": "상품 업데이트 성공",
             "data": null
        }
     ```     
3. **상품 삭제**

   - **URL**: `/api/admin/product/{productId}`
   - **HTTP Method**: `DELETE`
   - **요청값**: 
     - `productId` (Path Variable): 삭제할 상품 Id
   - **반환값**:

       ```json
       {
         "statusCode": 200,
         "message": "상품 삭제 성공",
         "data": null
       }
       ```
  
4. **브랜드 생성**
   - **URL**: `/api/admin/brand`
   - **HTTP Method**: `POST`
   - **요청값**:

       ```json
       {
         "brandName": "string"   // 생성할 브랜드 이름
       }
       ```

   - **반환값**:

       ```json
       {
         "statusCode": 201,
         "message": "브랜드 생성 성공",
         "data": {
           "id": "long"  // 생성된 브랜드 ID
         }
       }
       ```
  
## 빌드 및 실행 방법

1. 빌드

```bash
./gradlew clean build
```

2. 테스트 실행

```bash
./gradlew test
```

3. 실행

```bash
./gradlew bootRun
```

> 서버가 성공적으로 시작되면 http://localhost:8080 로 접근하여 실행 가능합니다.

## 기타정보

### 개발환경

- `Java 17`, `Spring Boot`,  `JPA`, `MapStruct`, `QueryDsl`, `Gradle`,

### 테스트 도구

- `Junit5`, `Mockito`, `AssertJ`

### 데이터베이스

- `H2Database`
