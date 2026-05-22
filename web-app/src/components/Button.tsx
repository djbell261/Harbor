import type { ButtonHTMLAttributes, ReactNode } from 'react';

type ButtonVariant = 'primary' | 'secondary' | 'quiet' | 'danger';

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  children: ReactNode;
  variant?: ButtonVariant;
  fullWidth?: boolean;
}

const variants: Record<ButtonVariant, string> = {
  primary: 'border-harbor-blue bg-harbor-blue text-white hover:bg-[#0b5d7d]',
  secondary: 'border-harbor-blue bg-white text-harbor-blue hover:bg-blue-50',
  quiet: 'border-transparent bg-transparent text-harbor-blue hover:bg-blue-50',
  danger: 'border-red-300 bg-red-50 text-harbor-red hover:bg-red-100'
};

export function Button({
  children,
  className = '',
  fullWidth = false,
  type = 'button',
  variant = 'secondary',
  ...props
}: ButtonProps) {
  return (
    <button
      className={`inline-flex min-h-11 items-center justify-center border px-4 py-2 text-sm font-semibold transition focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-harbor-blue disabled:cursor-not-allowed disabled:opacity-60 ${
        variants[variant]
      } ${fullWidth ? 'w-full' : ''} ${className}`}
      type={type}
      {...props}
    >
      {children}
    </button>
  );
}
