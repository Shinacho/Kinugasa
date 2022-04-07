#include "kinugasa_game_input_GamePadConnection.h"
#include "lib\jni.h"
#include <d2d1.h>
#include <Xinput.h>

#pragma comment(lib,"Xinput.lib")


static XINPUT_STATE state;
static XINPUT_GAMEPAD gamepad;


JNIEXPORT jfloatArray JNICALL Java_kinugasa_game_input_GamePadConnection_getNativeState(JNIEnv* env, jclass thisj, jint plIdx) {
 jfloatArray result = env->NewFloatArray(kinugasa_game_input_GamePadConnection_LENGTH);
 ZeroMemory(&state, sizeof(XINPUT_STATE));
 DWORD connection = XInputGetState(plIdx, &state);
 gamepad = state.Gamepad;
 float stateData[kinugasa_game_input_GamePadConnection_LENGTH];
 stateData[kinugasa_game_input_GamePadConnection_BUTTON_A] = float(gamepad.wButtons & XINPUT_GAMEPAD_A);
 stateData[kinugasa_game_input_GamePadConnection_BUTTON_B] = float(gamepad.wButtons & XINPUT_GAMEPAD_B);
 stateData[kinugasa_game_input_GamePadConnection_BUTTON_X] = float(gamepad.wButtons & XINPUT_GAMEPAD_X);
 stateData[kinugasa_game_input_GamePadConnection_BUTTON_Y] = float(gamepad.wButtons & XINPUT_GAMEPAD_Y);
 stateData[kinugasa_game_input_GamePadConnection_BUTTON_LB] = float(gamepad.wButtons & XINPUT_GAMEPAD_LEFT_SHOULDER);
 stateData[kinugasa_game_input_GamePadConnection_BUTTON_RB] = float(gamepad.wButtons & XINPUT_GAMEPAD_RIGHT_SHOULDER);
 stateData[kinugasa_game_input_GamePadConnection_BUTTON_LEFT_STICK] = float(gamepad.wButtons & XINPUT_GAMEPAD_LEFT_THUMB);
 stateData[kinugasa_game_input_GamePadConnection_BUTTON_RIGHT_STICK] = float(gamepad.wButtons & XINPUT_GAMEPAD_RIGHT_THUMB);
 stateData[kinugasa_game_input_GamePadConnection_BUTTON_POV_UP] = float(gamepad.wButtons & XINPUT_GAMEPAD_DPAD_UP);
 stateData[kinugasa_game_input_GamePadConnection_BUTTON_POV_DOWN] = float(gamepad.wButtons & XINPUT_GAMEPAD_DPAD_DOWN);
 stateData[kinugasa_game_input_GamePadConnection_BUTTON_POV_LEFT] = float(gamepad.wButtons & XINPUT_GAMEPAD_DPAD_LEFT);
 stateData[kinugasa_game_input_GamePadConnection_BUTTON_POV_RIGHT] = float(gamepad.wButtons & XINPUT_GAMEPAD_DPAD_RIGHT);
 stateData[kinugasa_game_input_GamePadConnection_BUTTON_START] = float(gamepad.wButtons & XINPUT_GAMEPAD_START);
 stateData[kinugasa_game_input_GamePadConnection_BUTTON_BACK] = float(gamepad.wButtons & XINPUT_GAMEPAD_BACK);
 stateData[kinugasa_game_input_GamePadConnection_TRIGGER_LEFT] = (float)gamepad.bLeftTrigger / (float)kinugasa_game_input_GamePadConnection_TRIGGER_MAX;
 stateData[kinugasa_game_input_GamePadConnection_TRIGGER_RIGHT] = (float)gamepad.bRightTrigger / (float)kinugasa_game_input_GamePadConnection_TRIGGER_MAX;
 stateData[kinugasa_game_input_GamePadConnection_THUMB_STICK_LEFT_X] = (float)gamepad.sThumbLX / (float)kinugasa_game_input_GamePadConnection_THUMSTICK_ABS_MAX * 2.0f;
 stateData[kinugasa_game_input_GamePadConnection_THUMB_STICK_LEFT_Y] = (float)gamepad.sThumbLY / (float)kinugasa_game_input_GamePadConnection_THUMSTICK_ABS_MAX * 2.0f;
 stateData[kinugasa_game_input_GamePadConnection_THUMB_STICK_RIGHT_X] = (float)gamepad.sThumbRX / (float)kinugasa_game_input_GamePadConnection_THUMSTICK_ABS_MAX * 2.0f;
 stateData[kinugasa_game_input_GamePadConnection_THUMB_STICK_RIGHT_Y] = (float)gamepad.sThumbRY / (float)kinugasa_game_input_GamePadConnection_THUMSTICK_ABS_MAX * 2.0f;
 stateData[kinugasa_game_input_GamePadConnection_CONNECTION] = (connection == ERROR_SUCCESS);
 jfloat* aryP = env->GetFloatArrayElements(result, NULL);
 for (int i = 0; i < kinugasa_game_input_GamePadConnection_LENGTH; i++) aryP[i] = stateData[i];
 env->ReleaseFloatArrayElements(result, aryP, 0);
 return result;
}

JNIEXPORT void JNICALL Java_kinugasa_game_input_GamePadConnection_free(JNIEnv* env, jclass thisj) {
 free(&state);
 free(&gamepad);
}
