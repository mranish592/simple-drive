import { createSlice } from "@reduxjs/toolkit";
import type { PayloadAction } from "@reduxjs/toolkit";

export interface AuthState {
    isAuthenticated: boolean;
    name: string | null;
    accessToken: string | null;
}
const initialState: AuthState = {
    isAuthenticated: false,
    name: localStorage.getItem("name"),
    accessToken: null,
};
export const authSlice = createSlice({
    name: "auth",
    initialState,
    reducers: {
        login: (state, action: PayloadAction<{ name: string; accessToken: string }>) => {
            state.isAuthenticated = true;
            state.name = action.payload.name;
            localStorage.setItem("name", action.payload.name);
            state.accessToken = action.payload.accessToken;
        },
        refresh: (state, action: PayloadAction<{ accessToken: string }>) => {
            state.accessToken = action.payload.accessToken;
            state.isAuthenticated = true;
        },
        logout: (state) => {
            state.isAuthenticated = false;
            state.name = null;
            localStorage.removeItem("name");
            state.accessToken = null;
        },
    },
});

export const { login, refresh, logout } = authSlice.actions;
export default authSlice.reducer;
