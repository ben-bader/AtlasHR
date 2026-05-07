"use client"

import React, { ReactNode, ErrorInfo } from "react"
import { AlertTriangle, RefreshCw } from "lucide-react"
import { Button } from "@/components/ui/button"

interface Props {
  children: ReactNode
  fallback?: ReactNode
}

interface State {
  hasError: boolean
  error: Error | null
  errorInfo: ErrorInfo | null
}

/**
 * Error Boundary Component
 * 
 * Catches JavaScript errors anywhere in the child component tree,
 * logs those errors, and displays a fallback UI instead of crashing the whole app.
 * 
 * Usage:
 * <ErrorBoundary>
 *   <YourComponent />
 * </ErrorBoundary>
 */
export class ErrorBoundary extends React.Component<Props, State> {
  public constructor(props: Props) {
    super(props)
    this.state = {
      hasError: false,
      error: null,
      errorInfo: null,
    }
  }

  public static getDerivedStateFromError(error: Error): State {
    return {
      hasError: true,
      error,
      errorInfo: null,
    }
  }

  public componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error("Error Boundary caught an error:", error, errorInfo)
    this.setState({
      error,
      errorInfo,
    })
  }

  private handleReset = () => {
    this.setState({
      hasError: false,
      error: null,
      errorInfo: null,
    })
  }

  public render() {
    if (this.state.hasError) {
      return (
        this.props.fallback || (
          <div className="min-h-screen flex items-center justify-center bg-gray-50">
            <div className="max-w-md w-full space-y-4 text-center">
              {/* Error Icon */}
              <div className="flex justify-center">
                <div className="bg-red-100 p-4 rounded-full">
                  <AlertTriangle className="w-8 h-8 text-red-600" />
                </div>
              </div>

              {/* Error Message */}
              <div className="space-y-2">
                <h1 className="text-2xl font-bold text-gray-900">
                  Something went wrong
                </h1>
                <p className="text-gray-600 text-sm">
                  An unexpected error occurred. Please try again or contact support
                  if the problem persists.
                </p>
              </div>

              {/* Error Details (Development Only) */}
              {process.env.NODE_ENV === "development" && this.state.error && (
                <div className="mt-4 p-4 bg-red-50 border border-red-200 rounded-lg">
                  <div className="text-left space-y-2">
                    <p className="text-xs font-semibold text-red-800 uppercase tracking-wide">
                      Error Details (Development Only)
                    </p>
                    <div className="bg-white p-3 rounded border border-red-100 overflow-auto max-h-40">
                      <code className="text-xs text-red-600 whitespace-pre-wrap break-words">
                        {this.state.error.toString()}
                      </code>
                    </div>
                    {this.state.errorInfo?.componentStack && (
                      <div className="bg-white p-3 rounded border border-red-100 overflow-auto max-h-40">
                        <p className="text-xs font-semibold text-red-800 mb-2">
                          Stack Trace
                        </p>
                        <code className="text-xs text-red-600 whitespace-pre-wrap break-words">
                          {this.state.errorInfo.componentStack}
                        </code>
                      </div>
                    )}
                  </div>
                </div>
              )}

              {/* Action Buttons */}
              <div className="flex gap-3 justify-center pt-4">
                <Button
                  onClick={this.handleReset}
                  className="inline-flex items-center gap-2"
                >
                  <RefreshCw className="w-4 h-4" />
                  Try Again
                </Button>
                <Button
                  variant="outline"
                  onClick={() => (window.location.href = "/")}
                >
                  Go Home
                </Button>
              </div>
            </div>
          </div>
        )
      )
    }

    return this.props.children
  }
}

export default ErrorBoundary
