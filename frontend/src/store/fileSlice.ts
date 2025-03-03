import { createSlice } from "@reduxjs/toolkit";
import type { PayloadAction } from "@reduxjs/toolkit";

export interface File {
    fileId: string;
    name: string;
    size: number;
    modified: Number;
}

export interface Files {
    files: File[];
}

const initialState: Files = {
    files: [],
};

export const fileSlice = createSlice({
    name: "file",
    initialState,
    reducers: {
        updateFiles: (state, action: PayloadAction<File[]>) => {
            state.files = action.payload;
        },
    },
});

export const { updateFiles } = fileSlice.actions;
