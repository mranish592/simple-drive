import { Label } from "@/components/ui/label";

("use client");

import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import axios from "axios";

import { Button } from "@/components/ui/button";
import { Form, FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { apiClient } from "@/api/api";

const formSchema = z.object({
    files: z
        .array(z.instanceof(File))
        .refine((files: Array<File>) => files.reduce((acc, file) => acc + file.size, 0) / 1024 / 1024 < 10, { message: "File size must be less than 10MB." }),
});

export function UploadForm() {
    const navigate = useNavigate();
    // const form = useForm({});
    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
    });

    async function onSubmit(data: z.infer<typeof formSchema>) {
        console.log(data);
        const formData = new FormData();
        data.files.forEach((file) => {
            formData.append("files", file); // Append each file with the same key
        });

        const response = await apiClient.post("/upload", formData, {
            headers: {
                "Content-Type": "multipart/form-data",
                Authorization: "Bearer " + localStorage.getItem("accessToken"),
            },
        });
        console.log(response);
    }

    return (
        <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                <FormField
                    control={form.control}
                    name="files"
                    render={({ field }) => (
                        <FormItem>
                            <FormLabel>File</FormLabel>
                            <FormControl>
                                <Input
                                    id="picture"
                                    type="file"
                                    multiple
                                    onChange={(e) => {
                                        if (e.target.files) {
                                            field.onChange(Array.from(e.target.files)); // Convert FileList to an array
                                        }
                                    }}
                                />
                            </FormControl>
                            {/* <FormDescription>This is your public display name.</FormDescription> */}
                            <FormMessage />
                        </FormItem>
                    )}
                />
                <Button type="submit">Upload</Button>
            </form>
        </Form>
    );
}
