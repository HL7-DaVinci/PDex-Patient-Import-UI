import { Store } from "./store/index";
import { ComponentCustomProperties } from "vue";
declare module '@vue/runtime-core' {
    interface ComponentCustomProperties {
        $store: Store;
    }
}