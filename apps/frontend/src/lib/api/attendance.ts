import api from "@/lib/api/axios";
import type { ApiResponse, Attendance, DailyAttendance } from "@/lib/types";

async function unwrap<T>(promise: Promise<{ data: ApiResponse<T> }>): Promise<T> {
  const response = await promise;
  return response.data.data as T;
}

export const attendanceAPI = {
  getDailyAttendanceByDate: async (date: string): Promise<DailyAttendance> =>
    unwrap(api.get<ApiResponse<DailyAttendance>>(`/daily-attendance/date/${encodeURIComponent(date)}`)),

  getDailyAttendanceRange: async (start: string, end: string): Promise<DailyAttendance[]> =>
    unwrap(api.get<ApiResponse<DailyAttendance[]>>(`/daily-attendance/range`, {
      params: {
        start,
        end,
      },
    })),

  listDailyAttendance: async (page?: number, size?: number): Promise<DailyAttendance[]> =>
    unwrap(api.get<ApiResponse<DailyAttendance[]>>(`/daily-attendance`, {
      params: {
        page,
        size,
      },
    })),

  countDailyAttendance: async (): Promise<number> =>
    unwrap(api.get<ApiResponse<number>>(`/daily-attendance/count`)),

  checkIn: async (employeeId: string): Promise<Attendance> =>
    unwrap(api.post<ApiResponse<Attendance>>(`/attendances/check-in`, { employeeId })),

  checkOut: async (employeeId: string): Promise<Attendance> =>
    unwrap(api.post<ApiResponse<Attendance>>(`/attendances/check-out`, { employeeId })),
};
