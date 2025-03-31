export interface SessionSummaryDto {
  id: string;
  date: string;
  projectName: string;
  grossEarnings: {
    amount: number;
    currency: string;
  };
}
