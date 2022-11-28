#include <jni.h>
#include "include/NativeLog.h"
#include "include/context.h"

using namespace std;

std::shared_ptr<context> g_ctx;

void on_message(const std::shared_ptr<vsomeip::message> &_message) {
    int status;
    JNIEnv *env = NULL;
    status = g_ctx->vm->GetEnv((void **) &env, JNI_VERSION_1_6);
    if (status < 0) {
        status = g_ctx->vm->AttachCurrentThread(&env, NULL);
        if (status < 0) {
            env = NULL;
            return;
        }
    }

    jclass clz = env->GetObjectClass(g_ctx->app_object);
    if (clz == nullptr) { return; }
    //获取要回调的方法
    jmethodID jMethodId = env->GetMethodID(clz, "onMessage", "(IIII[B)V");
    if (jMethodId == nullptr) { return; }

    // 获取消息信息
    int serviceId = _message->get_service();
    int instanceId = _message->get_instance();
    int methodId = _message->get_method();
    int clientId = _message->get_client();
    std::shared_ptr<vsomeip::payload> its_payload = _message->get_payload();
    // 转jbyteArray
    jbyte *by = (jbyte *) its_payload->get_data();
    int dataSize = its_payload->get_length();
    jbyteArray jarray = env->NewByteArray(its_payload->get_length());

    env->SetByteArrayRegion(jarray, 0, dataSize, by);
    env->CallVoidMethod(g_ctx->app_object, jMethodId, serviceId, instanceId, methodId, clientId,
                        jarray);

    env->DeleteLocalRef(clz);
    g_ctx->vm->DetachCurrentThread();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_server_VSomeIPService_create(JNIEnv *env, jobject thiz, jstring app_name,
jobject service) {
// 初始化context
g_ctx = std::make_shared<context>();
env->GetJavaVM(&g_ctx->vm);

const char *appNameConverted = env->GetStringUTFChars(app_name, 0);
char *appNameCopied = (char *) malloc(sizeof(char) * (strlen(appNameConverted) + 1));
strcpy(appNameCopied, appNameConverted);
g_ctx->app = vsomeip::runtime::get()->create_application(appNameConverted);
g_ctx->app_name = appNameCopied;

//生成一个全局引用，回调的时候findclass才不会为null
jobject callback = env->NewGlobalRef(service);
g_ctx->app_object = callback;

// 释放资源
env->ReleaseStringUTFChars(app_name, appNameConverted);
}


extern "C"
JNIEXPORT jboolean JNICALL
        Java_com_example_server_VSomeIPService_init(JNIEnv *env, jobject thiz) {
bool ret = g_ctx->app->init();
return ret;
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_server_VSomeIPService_offer_1service(JNIEnv *env, jobject thiz, jint service_id,
jint instance_id) {
g_ctx->app->register_message_handler(service_id, instance_id, vsomeip::ANY_METHOD, on_message);
g_ctx->app->offer_service(service_id, instance_id);
}

bool subscriptionHandler(vsomeip::client_t client_,vsomeip::uid_t _uid,vsomeip::gid_t _gid, bool _subscribed) {
    LOGE("%s received client_t = %02X , subscribed = %d",__func__ ,client_,_subscribed);
    //客户端订阅成功，启动一个线程来间隔5s广播事件
    if(_subscribed){
    }
    return true;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_server_VSomeIPService_offer_1event(JNIEnv *env, jobject thiz, jint service_id,
jint instance_id, jint event_id,
jint event_group_id) {
//注册客户端订阅事件组
g_ctx->app->register_subscription_handler(service_id, instance_id, event_id,subscriptionHandler);
std::set<vsomeip::eventgroup_t> its_groups;
its_groups.insert(event_group_id);
g_ctx->app->offer_event(service_id, instance_id, event_id, its_groups,
        vsomeip_v3::event_type_e::ET_EVENT, std::chrono::milliseconds::zero(),
false,
true,
nullptr,
vsomeip::reliability_type_e::RT_UNRELIABLE);

}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_server_VSomeIPService_offer_1field(JNIEnv *env, jobject thiz, jint service_id,
jint instance_id, jint field_id,
jint field_group_id) {
std::set<vsomeip::eventgroup_t> its_groups;
its_groups.insert(field_group_id);
g_ctx->app->offer_event(service_id, instance_id, field_id, its_groups,
        vsomeip_v3::event_type_e::ET_FIELD);

}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_server_VSomeIPService_stop_1offer_1event(JNIEnv *env, jobject thiz,
        jint service_id, jint instance_id,
        jint event_id) {
g_ctx->app->stop_offer_event(service_id, instance_id, event_id);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_server_VSomeIPService_stop_1offer_1field(JNIEnv *env, jobject thiz,
        jint service_id, jint instance_id,
        jint field_id) {
g_ctx->app->stop_offer_event(service_id, instance_id, field_id);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_server_VSomeIPService_start(JNIEnv *env, jobject thiz) {
g_ctx->app->start();
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_server_VSomeIPService_stop(JNIEnv *env, jobject thiz) {
g_ctx->app->stop();
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_server_VSomeIPService_notify(JNIEnv *env, jobject thiz, jint service_id,
jint instance_id, jint event_id,
jbyteArray payload) {
std::shared_ptr<vsomeip::payload> its_payload = vsomeip::runtime::get()->create_payload();
jbyte *bBuffer = env->GetByteArrayElements(payload, 0);
int payloadLen = env->GetArrayLength(payload);
std::unique_ptr<vsomeip::byte_t[]> its_data(new vsomeip::byte_t[payloadLen]());
memcpy(its_data.get(), bBuffer, payloadLen);
its_payload->set_data(its_data.get(), payloadLen);
g_ctx->app->notify(service_id, instance_id, event_id, its_payload, true);
//    g_ctx->app->notify_one(service_id,instance_id,event_id,its_payload,client_id);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_server_VSomeIPService_send_1response(JNIEnv *env, jobject thiz, jint service_id,
jint instance_id, jint method_id,
jbyteArray payload) {
// TODO: 从等待响应的请求池中取出请求消息
std::shared_ptr<vsomeip::message> request = vsomeip::runtime::get()->create_request();
// 构建响应消息
std::shared_ptr<vsomeip::message> response = vsomeip::runtime::get()->create_response(request);
response->set_service(service_id);
response->set_instance(instance_id);
response->set_method(method_id);
// 填充Payload
std::shared_ptr<vsomeip::payload> its_payload = vsomeip::runtime::get()->create_payload();
jbyte *bBuffer = env->GetByteArrayElements(payload, 0);
int payloadLen = env->GetArrayLength(payload);
std::unique_ptr<vsomeip::byte_t[]> its_data(new vsomeip::byte_t[payloadLen]());
memcpy(its_data.get(), bBuffer, payloadLen);
its_payload->set_data(its_data.get(), payloadLen);
response->set_payload(its_payload);
// 发送响应
g_ctx->app->send(response);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_server_VSomeIPService_close(JNIEnv *env, jobject thiz) {
// 释放资源
env->DeleteGlobalRef(g_ctx->app_object);
free(g_ctx->app_name);
}
