import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";

import { Button } from "@/components/ui/button";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { Input } from "@/components/ui/input";

import { apiClient } from "@/api/api";

import { useSelector } from "react-redux";
import { RootState } from "@/store/store";

const formSchema = z.object({
    files: z
        .array(z.instanceof(File))
        .refine((files: Array<File>) => files.reduce((acc, file) => acc + file.size, 0) / 1024 / 1024 < 10, { message: "File size must be less than 10MB." }),
});

export function UploadForm() {
    const accessToken = useSelector((state: RootState) => state.auth.accessToken);

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
    });

    async function onSubmit(data: z.infer<typeof formSchema>) {
        console.log(data);
        const formData = new FormData();
        data.files.forEach((file) => {
            formData.append("files", file);
        });

        const response = await apiClient.post("/upload", formData, {
            headers: {
                "Content-Type": "multipart/form-data",
                Authorization: "Bearer " + accessToken,
            },
        });
        console.log(response);
    }

    return (
        <div className="mt-12">
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
        </div>
    );
}
