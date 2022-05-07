# Keyboard.kt

## Generic

ClassName<**T** : ViewBinding>

## Parameter

|name|type|
|---|---|
| **context** | Context |
| **keyboardInteractionListener** | KeyboardInteractionListener |

## fields

|name|type|exp|
| --- | --- | --- |
| **resources** | Resources | none |
| **config** | Configuration | none |
| **handler** | Handler | none |
| **layoutInflater** | LayoutInflater | none |
| **preference** | SharedPreferences | 설정을 가져옵니다 |
| **audioManager** | AudioManager | 오디오 매니저 서비스를 가져옵니다 |
| **keyboardLayout** | T | 키보드 레이아웃 뷰 바인딩을 가져옵니다 |
| **height** | Int | 키보드의 높이를 가져옵니다 <br> 기본값:<br>[KeyboardConstants.KB_DEFAULT_PORTRAIT_HEIGHT](KeyboardConstants.md)<br>[KeyboardConstants.KB_DEFAULT_LANDSCAPE_HEIGHT](KeyboardConstants.md) |
| **sound** | Int | 소리의 크기를 가져옵니다 <br>기본값: [KeyboardConstants.KB_DEFAULT_SOUND](KeyboardConstants.md) |
| **vibrate** | Int | 진동의 크기를 가져옵니다 <br>기본값: [KeyboardConstants.KB_DEFAULT_VIBRATE](KeyboardConstants.md) |
| **initialInterval** | Int | 첫 터치에 의한 입력 후 다음 터치까지 간격의 길이를 설정합니다. <br>기본값 : [KeyboardConstants.KB_DEFAULT_INITIAL_INTERVAL](KeyboardConstants.md) |
| **normalInterval** | Int | normalInterval 이후의 모든 간격의 길이를 설정합니다. <br>기본값 : [KeyboardConstants.KB_DEFAULT_NORMAL_INTERVAL](KeyboardConstants.md) |
| **capsStatus** | Keyboard.Caps | 키보드의 Caps 상태를 가져옵니다. |


## methods
|name|return type|arguments|exp|
| --- | --- | --- | --- |
| **vibrator** | Vibrator | none | 진동 서비스를 가져옵니다 |
| **layout** | View | none | keyboardLayout 의 root 를 가져옵니다 |
| **inputConnection** | InputConnection | none | InputConnection을 설정합니다.<br>설정된 값이 null 이 아니라면<br> onInputConnectionReady 가 호출됩니다. |
| **init** | Unit | none | 키보드를 초기화 합니다. <br> 초기화 전에  |
| **onInputConnectionReady** | Unit | InputConnection | inputConnection 가 설정이 되면 호출됩니다. |
| **onKeyClickEvent** | Unit | View, KeyLine.Item | 키보드의 버튼이 클릭이 되면 호출됩니다. |
| **onKeyLongClickEvent** | Boolean | View, KeyLine.Item | 키보드의 버튼이 길게 클릭이 되면 호출됩니다. |
| **onKeyTouchEvent** | Boolean | View, KeyLine.Item, MotionEvent | 키보드의 버튼을 터치하면 호출됩니다. |
| **onKeyRepeatEvent** | Unit | View, KeyLine.Item | 키보드의 버튼을 길게 누르고 있으면 반복 호출됩니다. |
| **onKeyboardChanged** | Unit | Int, Int | 입력창을 클릭해서 키보드를 띄우거나 뒤로가기해서 닫으면 호출됩니다. |
| **onKeyboardUpdate** | Unit | Keyboard.Event | 설정이 업데이트 되면 호출됩니다. |
| **Int.toDips** | Float | none | int를 dip(float) 값으로 변환합니다. |
| **Long.toDips** | Float | none | long을 dip(float) 값으로 변환합니다. |
| **Float.toDips** | Float | none | float을 dip(float) 값으로 변환합니다. |
| **Double.toDips** | Float | none | double을 dip(float) 값으로 변환합니다. |
| **coroutineContext** | CoroutineContext | none | coroutineContext 를 가져옵니다. |
| **playVibrate** | Unit | none | 진동합니다. |
| **playClick** | Unit | Int | 소리를 냅니다. |
| **destroyCoroutine** | Unit | none | 키보드 서비스가 종료 되었을때 호출됩니다. |

## enum class
```kotlin
enum class Caps {
	ON,
	OFF,
	FIXED
}

enum class Event {
	OPEN,
	CLOSE
}
```
