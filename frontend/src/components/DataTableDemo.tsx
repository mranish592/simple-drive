"use client";

import * as React from "react";
import {
    ColumnDef,
    ColumnFiltersState,
    SortingState,
    VisibilityState,
    flexRender,
    getCoreRowModel,
    getFilteredRowModel,
    getPaginationRowModel,
    getSortedRowModel,
    useReactTable,
} from "@tanstack/react-table";
import { ArrowUpDown } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { File } from "@/store/fileSlice";
import { useDispatch, useSelector } from "react-redux";
import { RootState } from "@/store/store";
import { apiClient } from "@/api/api";
import { updateFiles } from "@/store/fileSlice";
import { AxiosProgressEvent } from "axios";

export function DataTableDemo() {
    const [sorting, setSorting] = React.useState<SortingState>([]);
    const [columnFilters, setColumnFilters] = React.useState<ColumnFiltersState>([]);
    const [columnVisibility, setColumnVisibility] = React.useState<VisibilityState>({});
    const [rowSelection, setRowSelection] = React.useState({});

    const files = useSelector((state: RootState) => state.files);
    const accessToken = useSelector((state: RootState) => state.auth.accessToken);
    const dispatch = useDispatch();
    const data = files.files;

    const columns: ColumnDef<File>[] = [
        {
            accessorKey: "name",
            header: ({ column }) => {
                return (
                    <Button variant="ghost" onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}>
                        Name
                        <ArrowUpDown />
                    </Button>
                );
            },
            cell: ({ row }) => <div className="lowercase">{row.getValue("name")}</div>,
        },
        {
            accessorKey: "size",
            header: ({ column }) => {
                return (
                    <div className="text-right">
                        <Button variant="ghost" onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}>
                            Size
                            <ArrowUpDown />
                        </Button>
                    </div>
                );
            },
            cell: ({ row }) => {
                const size = row.getValue<number>("size");
                var displaySize: number = size;
                var displayUnits = "B";
                if (size > 1000 * 1000 * 1000) {
                    displaySize = displaySize / (1000 * 1000 * 1000);
                    displayUnits = "GB";
                } else if (size > 1000 * 1000) {
                    displaySize = displaySize / (1000 * 1000);
                    displayUnits = "MB";
                } else if (size > 1000) {
                    displaySize = displaySize / 1000;
                    displayUnits = "KB";
                }
                return (
                    <div className="text-right font-medium">
                        {displaySize.toFixed(1)} {displayUnits}
                    </div>
                );
            },
        },
        {
            accessorKey: "modified",
            header: () => <div className="text-right">Modified</div>,
            cell: ({ row }) => {
                const modified = row.getValue<number>("modified");
                const date = new Date(modified).toLocaleDateString();
                return <div className="text-right font-medium">{date}</div>;
            },
        },
        {
            accessorKey: "fileId",
            header: () => <div className="text-right"></div>,
            cell: ({ row }) => {
                const fileId = row.getValue<number>("fileId");
                const downloadHandler = () => {
                    apiClient
                        .get("/download", {
                            headers: { Authorization: "Bearer " + accessToken },
                            params: {
                                fileId: fileId,
                            },
                            responseType: "blob", // important
                            onDownloadProgress: (progressEvent: AxiosProgressEvent) => {
                                if (progressEvent.total != null) {
                                    let percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
                                    console.log(percentCompleted);
                                }
                            },
                        })
                        .then((response) => {
                            const url = window.URL.createObjectURL(new Blob([response.data]));
                            const link = document.createElement("a");
                            link.href = url;
                            const fileName = row.getValue<string>("name");
                            link.setAttribute("download", fileName);
                            document.body.appendChild(link);
                            link.click();
                        })
                        .catch((error) => {
                            console.warn("Errore: " + error.message);
                            return [];
                        });
                };
                return (
                    <div className="text-right font-medium">
                        <Button onClick={downloadHandler}>Download</Button>
                    </div>
                );
            },
        },
    ];

    const table = useReactTable({
        data,
        columns,
        onSortingChange: setSorting,
        onColumnFiltersChange: setColumnFilters,
        getCoreRowModel: getCoreRowModel(),
        getPaginationRowModel: getPaginationRowModel(),
        getSortedRowModel: getSortedRowModel(),
        getFilteredRowModel: getFilteredRowModel(),
        onColumnVisibilityChange: setColumnVisibility,
        onRowSelectionChange: setRowSelection,
        state: {
            sorting,
            columnFilters,
            columnVisibility,
            rowSelection,
        },
    });

    const handleList = () => {
        apiClient
            .get("/list", {
                headers: { Authorization: "Bearer " + accessToken },
            })
            .then((response) => {
                console.log(response);
                const files = response.data.map((file: any) => {
                    return {
                        fileId: file.fileId,
                        name: file.fileName,
                        size: file.size,
                        modified: file.createdOn,
                    };
                });
                dispatch(updateFiles(files));
            });
    };

    React.useEffect(() => {
        handleList();
    }, [accessToken]);
    return (
        <div className="w-full">
            <div className="flex items-center py-4">
                <Input
                    placeholder="Filter files by name"
                    value={(table.getColumn("name")?.getFilterValue() as string) ?? ""}
                    onChange={(event) => table.getColumn("name")?.setFilterValue(event.target.value)}
                    className="max-w-sm"
                />
            </div>
            <div className="rounded-md border">
                <Table>
                    <TableHeader>
                        {table.getHeaderGroups().map((headerGroup) => (
                            <TableRow key={headerGroup.id}>
                                {headerGroup.headers.map((header) => {
                                    return (
                                        <TableHead key={header.id}>
                                            {header.isPlaceholder ? null : flexRender(header.column.columnDef.header, header.getContext())}
                                        </TableHead>
                                    );
                                })}
                            </TableRow>
                        ))}
                    </TableHeader>
                    <TableBody>
                        {table.getRowModel().rows?.length ? (
                            table.getRowModel().rows.map((row) => (
                                <TableRow key={row.id} data-state={row.getIsSelected() && "selected"}>
                                    {row.getVisibleCells().map((cell) => (
                                        <TableCell key={cell.id}>{flexRender(cell.column.columnDef.cell, cell.getContext())}</TableCell>
                                    ))}
                                </TableRow>
                            ))
                        ) : (
                            <TableRow>
                                <TableCell colSpan={columns.length} className="h-24 text-center">
                                    No results.
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </div>
        </div>
    );
}
