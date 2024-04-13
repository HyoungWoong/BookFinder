## 테스트
api key 는 local.properties 에 저장해서 사용해서 Github 상에 올리진 않았습니다. 테스트 하실 때 `API_KEY` 라는 키값을 추가해서 테스트 부탁드립니다.
## 구조
BookFinder 앱은 총 3개의 모듈로 구성되어 있습니다.
- core: 다른 앱과 도메인적 의존성이 없어서 재사용할 수 있고 Serializer, Pref 등의 기반 기능을 제공합니다.
- data: BookFinder 앱의 도메인 의존성을 가지고 있으며 BookFinder 앱에서 사용할 데이터를 저장 및 제공합니다.
- app: 위 두 모듈을 사용해서 BookFinder 앱을 구현합니다.

``` mermaid
flowchart LR
  A[app] --> B[data]
  A --> C[core]
  B --> C
```
## 사용한 라이브러리
#### hilt
액티비티나 Fragment 를 쉽게 Inject 하기 위해서 hilt 라이브러리를 사용했습니다.
#### coil-compose
비동기 이미지로딩을 위해 coil 을 사용했습니다. Glide 에서 만든 compose 는 아직 베타버전이라서 좀 더 안정적인 릴리즈 버전을 사용하기 위해 coil 을 선택했습니다.
#### Moshi
Serialize 과정을 추상화 하기 위해 Serializer 인터페이스를 만들어서 사용하고 있습니다. Kotlin Serialize 도 Serialize 라이브러리중 하나로 고민했지만 Type 인터페이스로 Serializer 를 찾을 수 없고 encode, decode 함수들이 inline 함수로 작성되어 있었기 때문에 제가 만들어둔 Serializer 인터페이스와 통합하기 어렵다고 생각했습니다.
반면에 Moshi 는 Serializer 인터페이스와 통합하기가 비교적 쉬웠기 때문에 Moshi 를 사용했습니다.
## 고민했던 점
### 화면간 코드의 분리
Compose 를 사용했을 때 화면을 이동하는 것을 Root Compose 를 변경하는 것으로 구현하는 걸 종종 봤습니다. 이렇게 구현할 경우 하나의 액티비티 안에서 너무 많은 의존성(ViewModel 등)과 책임을 가지고 있는 것 같아서 화면별로 분리해서 설계를 시작했습니다.
보통 탭을 구현할 땐 각 탭 화면의 로직이 복잡하기 때문에 각각 Fragment 로 분리해서 작성하곤 합니다. 그래서 동일하게 2 개의 Fragment 로 분리했고 Fragment 마다 Compose 화면을 작성했습니다.

### 싱글 이벤트 처리
ViewModel 단에서 화면간 이동, 얼럿 띄우기, 토스트 띄우기 등등 일회성 이벤트를 처리할 경우 PublishProcessor(Rx), SharedFlow(Kotlin) 을 사용해서 처리할 수 있습니다. 이 데이터 또한 UiState 에 넣을 순 있지만 이렇게 하면 UiState 클래스가 너무 거대해지고 나중에 수정하기 어려워질 것 같다고 생각했습니다.
그래서 단일 이벤트를 처리하기 위한 용도의 signals 라는 SharedFlow 를 만들고 화면 전체에서 필요한 일회성 처리를 한 곳에서 하도록 했습니다. UiState 와는 별도의 클래스이기 때문에 각자 관리할 수 있고 수정하는 것도 비교적 쉬운 구조라고 생각합니다.
