LOCAL_PATH := $(call my-dir)
TARGET_ARCH_ABI := armeabi

include $(CLEAR_VARS)
LOCAL_MODULE := libavcodec
LOCAL_SRC_FILES := lib/ffmpeg/$(TARGET_ARCH_ABI)/lib/$(LOCAL_MODULE).so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/lib/ffmpeg/$(TARGET_ARCH_ABI)/include
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libavcore
LOCAL_SRC_FILES := lib/ffmpeg/$(TARGET_ARCH_ABI)/lib/$(LOCAL_MODULE).so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/lib/ffmpeg/$(TARGET_ARCH_ABI)/include
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libavdevice
LOCAL_SRC_FILES := lib/ffmpeg/$(TARGET_ARCH_ABI)/lib/$(LOCAL_MODULE).so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/lib/ffmpeg/$(TARGET_ARCH_ABI)/include
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libavfilter
LOCAL_SRC_FILES := lib/ffmpeg/$(TARGET_ARCH_ABI)/lib/$(LOCAL_MODULE).so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/lib/ffmpeg/$(TARGET_ARCH_ABI)/include
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libavformat
LOCAL_SRC_FILES := lib/ffmpeg/$(TARGET_ARCH_ABI)/lib/$(LOCAL_MODULE).so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/lib/ffmpeg/$(TARGET_ARCH_ABI)/include
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libavutil
LOCAL_SRC_FILES := lib/ffmpeg/$(TARGET_ARCH_ABI)/lib/$(LOCAL_MODULE).so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/lib/ffmpeg/$(TARGET_ARCH_ABI)/include
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libswscale
LOCAL_SRC_FILES := lib/ffmpeg/$(TARGET_ARCH_ABI)/lib/$(LOCAL_MODULE).so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/lib/ffmpeg/$(TARGET_ARCH_ABI)/include
include $(PREBUILT_SHARED_LIBRARY)


include $(CLEAR_VARS)
LOCAL_SHARED_LIBRARIES := libavcore libavdevice libavfilter libavutil libswscale libavformat
LOCAL_LDLIBS := -L$(NDK_PLATFORMS_ROOT)/$(TARGET_PLATFORM)/arch-arm/usr/lib\
	-L$(LOCAL_PATH)/lib/ffmpeg/$(TARGET_ARCH_ABI)/lib \
	-lavcore \
	-lavformat \
	-lavcodec \
	-lavdevice \
	-lavfilter \
	-lavutil \
	-lswscale \
	-llog \
	-lz \
	-ldl \
	-lgcc

LOCAL_MODULE    := screenshot
LOCAL_SRC_FILES := screenshot.c colorspaceconv.c rgb2yuv.c capturethread.c encodethread.c videothread.c network.c debug.c

include $(BUILD_SHARED_LIBRARY)