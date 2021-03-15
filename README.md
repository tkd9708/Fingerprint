# 안드로이드 지문인식 기능탈취를 통한 강제 광고 삽입
2020.03 ~ 04 / 2020.08 ~ 09
<br/>

## 졸업작품
**Foreground** service와 **Background** service의 충돌을 유발하여 강제적으로 광고를 띄운다.
스마트폰에 저장된 모든 어플리케이션 목록을 출력한 후, 필요 시간동안 중지시키는 app을 통해 유도한다.
<br/>

## Stack
* Android studio ver 3.5.3
* Android ver 7.0-7.1 (Nougat)
* Android ver 9.0 (Pie)
<br/>

## Permission
* Overlay
* Accessibility
* Fingerprint
* Biometric
<br/>

## 기능
* 스마트폰에 저장되어 있는 모든 application 목록을 출력한다.
<img src="https://user-images.githubusercontent.com/55429998/111227009-438a9680-8625-11eb-9ccc-6cc6923c286a.png" width="200" height="350">

* 선택한 어플의 설정할 시간을 선택한다.
<img src="https://user-images.githubusercontent.com/55429998/111227177-851b4180-8625-11eb-9521-cec61ce4c932.png" width="200" height="350">

* 설정 완료 시, 해당 어플은 설정한 시간까지 실행되지 않는다.
<img src="https://user-images.githubusercontent.com/55429998/111227218-949a8a80-8625-11eb-9c56-d706c7ec680d.png" width="200" height="350">

* 설정한 시간이 도달할 시, 잠금 해제된다.
<img src="https://user-images.githubusercontent.com/55429998/111227326-bd228480-8625-11eb-9010-b63216479779.png" width="200" height="350">

* 어플을 실행함으로서, Background에서 사용자의 지문인식 행동을 감시하는 service를 실행시킨다.
* 사용자가 지문인식을 사용할 시, Background service에서 먼저 작동시켜 지문인식을 풀지 못하도록 한다.
<img src="https://user-images.githubusercontent.com/55429998/111227450-ee9b5000-8625-11eb-952e-fd80fb065fde.png" width="200" height="350">

* 반복적으로 지문인식 시도 시, 불법광고를 띄운다.
<img src="https://user-images.githubusercontent.com/55429998/111227512-05da3d80-8626-11eb-8088-5fdacfe92566.png" width="200" height="350">
