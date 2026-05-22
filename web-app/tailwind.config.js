/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        harbor: {
          ink: '#172026',
          muted: '#53636f',
          line: '#d7e0e5',
          wash: '#f6f8f9',
          blue: '#0f6b8f',
          green: '#24745c',
          amber: '#9a5b13',
          red: '#a23b3b'
        }
      },
      boxShadow: {
        soft: '0 1px 2px rgba(15, 23, 42, 0.08)'
      }
    }
  },
  plugins: []
};
