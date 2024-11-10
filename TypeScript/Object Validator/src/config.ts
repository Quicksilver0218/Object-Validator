export type Condition = {
    readonly type: string;
    readonly field?: string;
    readonly arg?: string | null;
    readonly args?: (string | null)[];
    readonly conditions?: Condition[];
};

export type Rule = {
    readonly condition: Condition;
    readonly id?: number;
    readonly errorMessage?: string;
};