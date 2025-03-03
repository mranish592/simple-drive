import { configureStore } from "@reduxjs/toolkit";
import { authSlice } from "./authSlice";
import { fileSlice } from "./fileSlice";

export const store = configureStore({
    reducer: {
        auth: authSlice.reducer,
        files: fileSlice.reducer,
    },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
